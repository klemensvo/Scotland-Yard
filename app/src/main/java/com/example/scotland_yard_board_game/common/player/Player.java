package com.example.scotland_yard_board_game.common.player;

import com.example.scotland_yard_board_game.common.Color;
import com.example.scotland_yard_board_game.common.Station;

public interface Player {

    int getId();
    String getNickname();
    Color getColor();
    void setColor(Color color);

    boolean useItem(int itemid);
    void setPosition(Station position);

    boolean validMove(int stationId, int type);
    int getConnectionId();
    Station getPosition();

    /*
    int getmoves();
    void setmoves(int moves); */
}

