package com.example.scotland_yard_board_game.common;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class StationDatabase {
    private ArrayList<Station> stationlist;
    private int[] misterXStartStations;
    private int[] detectiveStartStations;
    private Context context;

    public StationDatabase(Context context) {
        this.context = context;
        buildDatabase();
    }

    private void buildDatabase() {
        this.misterXStartStations = new int[]{35, 45, 51, 71, 78, 104, 106, 127, 132, 146, 166, 170, 172};
        this.detectiveStartStations = new int[]{13, 26, 29, 34, 50, 53, 91, 94, 103, 112, 117, 123, 138, 141, 155, 174};

        //Reads file to string
        String json = null;
        try {
            InputStream input = context.getAssets().open("stations.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //Use gson library to convert String to Station objects
        Gson gson = new Gson();
        Type StationListType = new TypeToken<ArrayList<Station>>(){}.getType();
        this.stationlist = gson.fromJson(json, StationListType);
        Log.d(TAG,"Stations loaded Sucessfully!");
    }

    public Station getStation(int id){
        for (Station a: stationlist) {
          if( a.getId() == id){
              return a;
          }
        }
        return null;
    }

    public int[] getRandomStart(int numPlayers){
        int[] temp;
        int[] Start = new int[numPlayers];

        //MrX Start
        temp = this.misterXStartStations;
        Random random = new Random();
        int number = random.nextInt(temp.length-1);
        Start[0] = temp[number];
        
        //Detective Start
        temp = detectiveStartStations;
        for (int i = 1 ; i<numPlayers; i++){
            number = random.nextInt(temp.length-1);
            if (temp[number]!=0){
                Start[i] = temp[number];
            }else{ //If start already taken, try until valid start found
                while(temp[number]==0){
                    number = random.nextInt(temp.length-1);
                    Start[i] = temp[number];
                }
            }
            temp[number] = 0;
            
        }

        return Start;
    }
}
