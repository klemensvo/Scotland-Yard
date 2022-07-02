package com.example.scotland_yard_board_game.server;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.esotericsoftware.kryonet.Server;
import com.example.scotland_yard_board_game.common.Color;
import com.example.scotland_yard_board_game.common.Station;
import com.example.scotland_yard_board_game.common.messages.GameStart;
import com.example.scotland_yard_board_game.common.messages.fromserver.ColorConfirmed;
import com.example.scotland_yard_board_game.common.messages.fromserver.ColorTaken;
import com.example.scotland_yard_board_game.common.messages.fromserver.DetectivesWon;
import com.example.scotland_yard_board_game.common.messages.fromserver.EndTurn;
import com.example.scotland_yard_board_game.common.messages.fromserver.InvalidMove;
import com.example.scotland_yard_board_game.common.messages.fromserver.TravelLog;
import com.example.scotland_yard_board_game.common.messages.fromserver.MrXWon;
import com.example.scotland_yard_board_game.common.messages.fromserver.NameTaken;
import com.example.scotland_yard_board_game.common.messages.fromserver.PlayerList;
import com.example.scotland_yard_board_game.common.messages.fromserver.StartTurn;
import com.example.scotland_yard_board_game.common.player.Detective;
import com.example.scotland_yard_board_game.common.player.MisterX;
import com.example.scotland_yard_board_game.common.player.Player;
import com.example.scotland_yard_board_game.common.StationDatabase;

import java.util.ArrayList;

public class ServerData {

    private final Server server;
    private final StationDatabase stationDatabase;
    private final ArrayList<Player> players = new ArrayList<>(6);
    private final PlayerList playerList = new PlayerList();
    private final TravelLog travelLog = new TravelLog();
    private boolean started = false;
    private int misterXTurns = 0;
    private int activePlayer;  //Which player is allowed to move
    private int[] playerOrder; //In which order players move

    public ServerData(Context context, Server server) {
        this.server = server;
        travelLog.travelLog = new int[24][2];

        this.stationDatabase = new StationDatabase(context);
        int[] testStart = stationDatabase.getRandomStart(4);
        for (int playerNumber : testStart) {
            Log.d(TAG, String.valueOf(playerNumber));
        }
        Station station = stationDatabase.getStation(1); // todo: hard-coded value
        Log.d(TAG, String.valueOf(station.getX()));
    }

    //Check if Player colour available
    public void playerColor(Color color, int connectionId) {
        for (Player player : players) {
            if (player.getColor() == color) {
                server.sendToTCP(connectionId, new ColorTaken());
            }
        }
        for (Player player : players) {
            if (player.getConnectionId() == connectionId) {
                player.setColor(color);
                server.sendToTCP(connectionId, new ColorConfirmed());
                updatePlayerList();
            }
        }

    }

    //Not implemented
    public boolean useItem(int clientId, int itemId) { //will be used for mrx
        for (Player player : players) {
            if (player.getId() == clientId) {
                return player.useItem(itemId);
            }
        }
        return false;
    }

    //Validates player moves, triggers refresh of journeyTable and starts next players move
    // todo: continue refactoring here
    public void move(int connectionId, int stationId, int meansOfTransport, boolean isMisterX) {
        for (Player player : players) {
            if (player.getConnectionId() == connectionId && player.getConnectionId() == playerOrder[activePlayer]) {
                Log.d(TAG, stationId + " " + meansOfTransport);
                boolean valid = player.validMove(stationId, meansOfTransport);
                if (valid && stationId != players.get(0).getPosition().getId()) {
                    player.setPosition(stationDatabase.getStation(stationId));
                    updatePlayerList();
                    if (isMisterX && misterXTurns < travelLog.travelLog.length) {
                        travelLog.travelLog[misterXTurns][0] = meansOfTransport;
                        travelLog.travelLog[misterXTurns][1] = stationId;
                        misterXTurns++;
                        server.sendToAllTCP(travelLog);
                    }
                    server.sendToTCP(connectionId, new EndTurn());
                    activePlayer++;
                    startPlayerTurn();
                } else if (valid && stationId == players.get(0).getPosition().getId()) {
                    server.sendToAllTCP(new DetectivesWon());
                } else {
                    server.sendToTCP(connectionId, new InvalidMove());
                }
            } else if (player.getPosition().getId() == stationId && !(player instanceof MisterX)) {
                server.sendToTCP(connectionId, new InvalidMove());
            }
        }

    }

    //Connect player if space in lobby and game not started
    public boolean connectPlayer() {
        return !started && players.size() < 6;
    }

    // Player fully joins when he sends his nickname
    public void joinPlayer(int conid, String nickname, int type) {
        int playerId = players.size();
        for (Player a : players) {
            if (nickname.equals(a.getNickname())) {
                server.sendToTCP(conid, new NameTaken());
            }
        }
        if (type == 0) {
            players.add(new MisterX(playerId, conid, nickname));
        } else {
            players.add(new Detective(playerId, conid, nickname));
        }

        updatePlayerList();
    }


    //On game start distribute starting points and calculate turn order
    public void gameStart() {
        if (/*Clients.size() >= 2*/ true) {
            started = true;
            calculatePlayerOrder(players.size());
            int[] startpoints = stationDatabase.getRandomStart(players.size());
            int index = 1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) instanceof MisterX && startpoints[0] != 0) {
                    players.get(i).setPosition(stationDatabase.getStation(startpoints[0]));
                    startpoints[0] = 0;
                } else {
                    players.get(i).setPosition(stationDatabase.getStation(startpoints[index]));
                    index++;
                }
            }
            updatePlayerList();
            server.sendToAllTCP(new GameStart());
            startPlayerTurn();
        }

    }

    //Send updated PlayerList to all clients
    public void updatePlayerList() {
        playerList.Players = players;
        server.sendToAllTCP(playerList);
    }

    //Remove disconnected player from clients
    public void disconnectPlayer(int conid) {
        if (!players.isEmpty()) {
            for (Player a : players) {
                if (a.getConnectionId() == conid) {
                    players.remove(a.getId());
                }
            }
            updatePlayerList();
            calculatePlayerOrder(players.size());
        }

    }

    //Start player turn
    public void startPlayerTurn() {
        //If the last player did not land on MrX field on the last turn -> MrX winns
        if (activePlayer >= playerOrder.length && misterXTurns != travelLog.travelLog.length - 1) {
            activePlayer = 0;
        } else if (misterXTurns == travelLog.travelLog.length - 1) {
            server.sendToAllTCP(new MrXWon());
        }
        server.sendToTCP(playerOrder[activePlayer], new StartTurn());
    }

    //Calculate player turn order
    private void calculatePlayerOrder(int playerCount) {
        playerOrder = new int[playerCount];
        //Get MrX conid
        for (Player a : players) {
            if (a instanceof MisterX) {
                playerOrder[0] = a.getConnectionId();
            }
        }

        //Get order for other players
        int index = 1;
        for (Player a : players) {
            if (a instanceof Detective) {
                playerOrder[index] = a.getConnectionId();
                index++;
            }
        }
    }

}
