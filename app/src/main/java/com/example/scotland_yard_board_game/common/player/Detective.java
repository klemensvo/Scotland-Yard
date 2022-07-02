package com.example.scotland_yard_board_game.common.player;

import com.example.scotland_yard_board_game.common.Color;
import com.example.scotland_yard_board_game.common.Station;

public class Detective implements Player {
    private int id;
    private int connectionId;
    private String nickname;
    private int[] ticketInventory = {10, 8, 4}; //Taxi, Bus, Underground
    private Station position;
    private Color color;
    // private boolean turn;
    private int moves = 1;

    //Kryonet
    public Detective() {
    }

    public Detective(int clientid, int connectionId, String nickname) {
        this.id = clientid;
        this.connectionId = connectionId;
        this.nickname = nickname;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean validMove(int stationId, int type) { //Validate if station is neighbour and if sufficient tickets are available
        int[] neighbours = this.position.getNeighbours(type);
        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] == stationId) {
                switch (type) {
                    case 0:
                        return useItem(0);
                    case 1:
                        return useItem(1);
                    case 2:
                        return useItem(2);
                }
            }
        }
        return false;
    }

    public void setPosition(Station position) {
        this.position = position;
    }

    public Station getPosition() {
        return position;
    }

    //If item available -> use it, otherwise return false
    public boolean useItem(int itemid) {

        if (this.ticketInventory[itemid] > 0) {
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

    public int getmoves() {
        return moves;
    }

    public void setmoves(int moves) {
        this.moves = moves;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
