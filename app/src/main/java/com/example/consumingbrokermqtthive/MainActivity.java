package com.example.consumingbrokermqtthive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class MainActivity extends AppCompatActivity {

    private TextView campoTemperatura;
    private TextView campoUmidade;
    private Gson gson;
    private final String TAG = "MainActivity";
    private Mensagem msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.msg = new Mensagem();
        this.gson = new Gson();
        inicializarCampoTexto();
        inicializarWebSocket();
    }

    private void inicializarCampoTexto(){
        this.campoTemperatura = findViewById(R.id.main_textview_temperatura);
        this.campoUmidade = findViewById(R.id.main_textview_umidade);
    }

    private void inicializarWebSocket(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ClientManager clientManager = ClientManager.createClient();
                try {
                    clientManager.connectToServer(new ReceiverMessageWebSocketBroker(), new URI("ws://10.0.0.108:1880/ws/test"));
                } catch (DeploymentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class ReceiverMessageWebSocketBroker extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {

            Log.d(TAG,"Conexão com servidor realizada com sucesso");

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Log.d(TAG,message);

                    MainActivity.this.msg = MainActivity.this.gson.fromJson(message, Mensagem.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.campoTemperatura.setText(String.valueOf(MainActivity.this.msg.getTemperatura())+"°");
                            MainActivity.this.campoUmidade.setText(String.valueOf(MainActivity.this.msg.getUmidade())+"%");
                        }
                    });

                }
            });
        }


    }

}
