package com.example.scotland_yard_board_game.common.player;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.scotland_yard_board_game.common.Color;
import com.example.scotland_yard_board_game.common.Station;

public class MisterX implements Player {
    private int id;
    private int conId;
    private String nickname;
    // todo: change ticketInventory: has a different meaning in Detective...
    private int[] ticketInventory = {2, 0}; //Double move, Black Tickets
    private Station position;
    private Color color = Color.BLUE; // Colour.TRANSPARENT ; // todo: change to transparent later
    // private boolean turn;
    private int moves = 1;

    //Kryonet
    public MisterX() {
    }


    public MisterX(int clientid, int conId, String nickname) {
        this.id = clientid;
        this.conId = conId;
        this.nickname = nickname;
    }

    //MrX gets as many Double moves as there are detectives -> declared after game start
    public void setDoubleMove(int NumDetectives) {
        this.ticketInventory[1] = NumDetectives;
    }

    public void setPosition(Station position) {
        this.position = position;
    }

    public Station getPosition() {
        return position;
    }

    public boolean validMove(int stationId, int type) {
        int[] neighbours = this.position.getNeighbours(type);
        Log.d(TAG, String.valueOf(neighbours[0]));
        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] == stationId) {
                return true; // TODO: 5/5/2022 implement check for black ticket
            }
        }
        return false;
    }


    //If item available -> use it, otherwise return false
    public boolean useItem(int itemid) {

        if (this.ticketInventory[itemid] > 0) { // TODO: 4/30/2022 implement double move action
            this.ticketInventory[itemid] -= 1;
            return true;
        }

        return false;
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getmoves() {
        return moves;
    }

    public void setmoves(int moves) {
        this.moves = moves;
    }

    public int getConnectionId() {
        return conId;
    }
}
