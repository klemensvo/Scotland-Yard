package com.example.scotland_yard_board_game.server;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.esotericsoftware.kryonet.Server;
import com.example.scotland_yard_board_game.common.KryoRegister;

import java.io.IOException;

public class ServerStart {
    private static final String TAG = "ServerStart";

    public ServerStart(Context context) throws IOException {
        // todo: is StrictMode necessary? works without it
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final Server server;
        final ServerData serverData;
        server = new Server();
        serverData = new ServerData(context, server);

        KryoRegister.registerClasses(server.getKryo());
        server.addListener(new ServerListener(serverData)); // todo: continue refactoring here

        server.start();
        server.bind(54555, 54777); // todo: is UDP necessary?
        Log.d(TAG, "Server Started!");
    }
}
