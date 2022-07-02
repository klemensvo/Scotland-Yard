package com.example.scotland_yard_board_game.common;

public class Station {
    private int id;
    private int x;
    private int y;
    private int[] taxiNeighbors;
    private int[] busNeighbors;
    private int[] undergroundNeighbors;
    private int[] ferryNeighbors;

    public Station() {
    } // todo: if commented out, GameScreen crashes

    public Station(int id, int x, int y, int[] taxiNeighbors,
                   int[] busNeighbors, int[] undergroundNeighbors, int[] ferryNeighbors) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.taxiNeighbors = taxiNeighbors;
        this.busNeighbors = busNeighbors;
        this.undergroundNeighbors = undergroundNeighbors;
        this.ferryNeighbors = ferryNeighbors;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[] getTaxiNeighbors() {
        return taxiNeighbors;
    }

    public int[] getBusNeighbors() {
        return busNeighbors;
    }

    public int[] getUndergroundNeighbors() {
        return undergroundNeighbors;
    }

    public int[] getFerryNeighbors() {
        return ferryNeighbors;
    }


    public int[] getNeighbours(int type) {
        switch (type) {

            case 0:
                return taxiNeighbors;
            case 1:
                return busNeighbors;
            case 2:
                return undergroundNeighbors;
            case 3:
                return ferryNeighbors;
        }
        return null;
    }
}
