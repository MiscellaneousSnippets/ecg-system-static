package com.example.abdulsalam.ecgandroidclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private GraphView graph;
    private final Activity context = this;
    private Socket mSocket;
    private TextView txtJson;

    {
        try {
            mSocket = IO.socket("http://192.168.43.27:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = findViewById(R.id.graph);
        mSocket.on("event", dataPacket);
        txtJson = findViewById(R.id.txtJson);
        mSocket.connect();
    }

    private Emitter.Listener dataPacket = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject inbound = (JSONObject) args[0];
                    JSONObject ecgArrayObject;
                    JSONArray ecgArray ;
                    String ecgStatus;
                    try {

                        ecgArrayObject = inbound.getJSONObject("ecgArray");
                        ecgArray = ecgArrayObject.getJSONArray("ecg");
                        ecgStatus = inbound.getString("status");
                        Toast.makeText(context, ecgStatus, Toast.LENGTH_SHORT).show();
                        txtJson.setText(ecgStatus);
                    } catch (JSONException e) {
                        return;
                    }

                    // add the value to view
                    ArrayList<String> listData = new ArrayList<>();
                    for (int i=0;i<ecgArray.length();i++){
                        try {
                            listData.add(ecgArray.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    //draw
                    DataPoint [] dataPoint = new DataPoint[listData.size()];

                    for (int i = 0; i <listData.size() ; i++) {
                        dataPoint[i] = new DataPoint(i,Float.parseFloat(listData.get(i)));

                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoint);
                    graph.addSeries(series);
                    
                    //

                }
            });
        }
    };
}
