package com.example.pcon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    EditText ipaddr;
    EditText portnum;

    // 변수 선언
    String serverIp  = null;
    int serverPort = 3403;

    String reciveMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipaddr = (EditText)findViewById(R.id.ipaddr_edit);
        portnum = (EditText)findViewById(R.id.portnum_edit);
    }

    public void ToDo (View v) {
        // 아두이노로 패킷 전송 동작
        if(v.getId() == R.id.send_btn) {
            serverIp = ipaddr.getText().toString();
            serverPort = Integer.parseInt(portnum.getText().toString());

            SendData sendData = new SendData(serverIp, serverPort);
            sendData.start();

        }
    }
    
    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_LONG).show();
        }
    }

    public class SendData extends Thread {
        String ip;
        int port;

        SendData(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            try {
                DatagramSocket socket = new DatagramSocket();
                InetAddress serverAddr = InetAddress.getByName(ip);

                byte[] buf = ("This is test").getBytes();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, port);

                socket.send(packet);
                Log.i("SendData", "send");

                socket.receive(packet);
                reciveMsg = new String(packet.getData());
                Log.i("SendData", reciveMsg);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SendData", "Send Fail");
            }
        }
    }
}
