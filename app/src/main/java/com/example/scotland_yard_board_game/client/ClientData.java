package com.example.scotland_yard_board_game.client;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.example.scotland_yard_board_game.common.Color;
import com.example.scotland_yard_board_game.common.StationDatabase;
import com.example.scotland_yard_board_game.common.messages.GameStart;
import com.example.scotland_yard_board_game.common.messages.fromclient.DetectiveNickname;
import com.example.scotland_yard_board_game.common.messages.fromclient.MrXNickname;
import com.example.scotland_yard_board_game.common.messages.fromserver.TravelLog;
import com.example.scotland_yard_board_game.common.messages.fromclient.Move;
import com.example.scotland_yard_board_game.common.messages.fromserver.PlayerList;
import com.example.scotland_yard_board_game.common.player.Player;

import java.net.InetAddress;
import java.util.ArrayList;

public class ClientData {

    private Client client;
    private StationDatabase stationDatabase;
    private ArrayList<Player> players = new ArrayList<>(6);
    private boolean started = false;
    private TravelLog travelLog = new TravelLog();
    private boolean ownturn = false;
    private boolean mrx;
    private String[] nicknames = new String[6];
    private GameScreen gameScreen;
    private LobbyScreen lobbyScreen;

    public ClientData(Context context, Client client, boolean mrx) {
        this.client = client;
        this.mrx = mrx;
        travelLog.travelLog = new int[24][2];
        this.stationDatabase = new StationDatabase(context);
    }


// TODO: 6/9/2022 implement client functions including turns
// TODO: 6/9/2022  how to handle local player instance? via array or local variable?
// TODO: 6/9/2022 handle updating of gamescreen on gamestate update

    //Search for server, if found, connect.
    public void connectServer() {
        try {
            InetAddress address = client.discoverHost(54777, 5000);
            Log.d(TAG, String.valueOf(address));
            client.connect(5000, address, 54555, 54777);
            Log.d(TAG, "Server connection successful");

        } catch (Exception e) {
            Log.i(TAG, "Client connection error: ", e);
            // TODO: 6/9/2022 handle if no server found
        }

    }

    // Player sends his nickname
    public void setNickname(String nickname) {
        if (mrx) {
            MrXNickname name = new MrXNickname();
            name.nickname = nickname;
            client.sendTCP(name);
        } else {
            DetectiveNickname name = new DetectiveNickname();
            name.nickname = nickname;
            client.sendTCP(name);
        }

    }

    //Sends GameStart message to server
    public void gameStart() {
        started = true;
        client.sendTCP(new GameStart());
    }

    //Handles what happens if server sends game started
    public void gameStarted() {
        started = true;
        Log.d(TAG, String.valueOf(players.get(0).getPosition().getId()));
        gameScreen.updatePlayerBoardCoordinates(players);
    }

    //Player chooses colour -> server checks if available
    public void chooseColour(Color color) {
        client.sendTCP(color);
    }

    public void colourTaken() {
        // TODO: 6/9/2022  implement what happens if colour is taken
    }


    public boolean useItem(int itemid) { //will be used for mrx double turn
        // TODO: 6/9/2022 implement for mrx -> after turns are implemented
        return false;
    }

    //Server validates move -> sends updated player list back
    public void validateMove(int stationid, int type) {
        Move move = new Move();
        move.type = type;
        move.station = stationid;
        move.mrx = mrx;
        client.sendTCP(move);
    }

    public void invalidMove() {
        // TODO: 6/11/2022 handle invalid move
    }

    public void updatePlayers(PlayerList list) {
        this.players = list.Players;
        updateNicknames();
        if (started) {
            gameScreen.updatePlayerBoardCoordinates(players);
        }
    }

    public void updateJourneyTable(TravelLog jtable) {
        this.travelLog = jtable;

    }

    //List of Nicknames for lobby
    private void updateNicknames() {
        for (Player a : players) {
            nicknames[a.getId()] = a.getNickname();
        }
        if (lobbyScreen != null && !started) {
            lobbyScreen.displayNicknames(nicknames);
        }

    }

    public String[] getNicknames() {
        return nicknames;
    }

    public void startTurn() {
        this.ownturn = true;
    }

    public void endTurn() {
        this.ownturn = false;
    }

    //What happens when someone wins
    public void won(int i) {
    }

    public void disconnectPlayer() {
        // TODO: 6/9/2022 decide how disconnecting is handled
    }

    public void disconnectClient() {
        // TODO: 6/11/2022 what does the client do on disconnect
    }

    public void serverFull() {
        // TODO: 6/9/2022 what to do if server full
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void setLobbyScreen(LobbyScreen lobbyScreen) {
        this.lobbyScreen = lobbyScreen;
    }

    public StationDatabase getStationDatabase() {
        return stationDatabase;
    }


}
