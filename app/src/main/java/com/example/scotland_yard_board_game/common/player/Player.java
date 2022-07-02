package com.example.scotland_yard_board_game.common.player;

import com.example.scotland_yard_board_game.common.Colour;
import com.example.scotland_yard_board_game.common.Station;

public interface Player {

    int getId();
    String getNickname();
    Colour getColour();
    void setColour(Colour colour);


    boolean useItem(int itemid);
    void setPosition(Station position);

    boolean validMove(int stationid, int type);
    int getConnectionId();
    Station getPosition();

    /*
    int getmoves();
    void setmoves(int moves); */
}

