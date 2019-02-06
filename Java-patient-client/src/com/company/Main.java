package com.company;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {


    public static void main(String[] args) throws URISyntaxException, JSONException {

        JSONArray jsonArray = new JSONArray();

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\XPS-13\\Desktop\\Senior\\Static\\Data\\afib.csv"))) {
            String line;
/*            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                jsonArray.put(values[1]);
            }*/
            for (int i = 0; i < 3600 ; i++) {
                line = br.readLine();
                String[] values = line.split(",");
                jsonArray.put(values[1]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("ecg",jsonArray);

        final Socket socket = IO.socket("http://localhost:3000");
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                socket.emit("foo", jsonObject);

            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                System.out.println(args[0].toString());
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();
    }
}
