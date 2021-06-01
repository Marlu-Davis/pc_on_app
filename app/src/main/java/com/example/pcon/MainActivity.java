package com.example.pcon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    EditText ipaddr;
    EditText portnum;

    // 변수 선언
    boolean isConnected = false;
    String serverIp  = null;
    int serverPort = 3403;
    Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipaddr = (EditText)findViewById(R.id.ipaddr_edit);
        portnum = (EditText)findViewById(R.id.portnum_edit);
    }

    public void ToDo (View v) {
        // 서버 연결 버튼 동작
        if(v.getId() == R.id.connect_btn) {
            serverIp = ipaddr.getText().toString();
            serverPort = Integer.parseInt(portnum.getText().toString());

            Toast.makeText(getApplicationContext(), "Try Connect", Toast.LENGTH_SHORT).show();

            ConnectThread thread = new ConnectThread(serverIp, serverPort);
            thread.start();
        }

        // 아두이노로 패킷 전송 동작
        if(v.getId() == R.id.send_btn) {
            if (!isConnected)
                Toast.makeText(getApplicationContext(), "Connect Server Requied", Toast.LENGTH_LONG).show();
            else {
                SenderThread thread = new SenderThread();
                thread.start();
                Toast.makeText(getApplicationContext(), "Send ON to PC", Toast.LENGTH_LONG).show();
            }
        }

    }

    public class ConnectThread extends Thread {
        String ip;
        int port;

        ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;

            Toast.makeText(getApplicationContext(), "connecting to " + ip + " " + port, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InetAddress addr = socket.getInetAddress();
                        String tmp = addr.getHostAddress();
                        isConnected = true;
                        Toast.makeText(getApplicationContext(), "connected" + tmp, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (UnknownHostException uhe) { // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException ioe) { // 소켓 생성 과정에서 I/O 에러 발생.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 네트워크 응답 없음", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (SecurityException se) { // security manager에서 허용되지 않은 기능 수행.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IllegalArgumentException le) { // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), " Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public class SenderThread extends Thread {
        int bytes;
        String tmp = "11";

        SenderThread() { }

        @Override
        public void run() {

            try {
                byte[] data = tmp.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Toast.makeText(getApplicationContext(), "Send to PC", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close(); //소켓을 닫는다.
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}