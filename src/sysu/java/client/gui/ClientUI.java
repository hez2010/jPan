package sysu.java.client.gui;

import java.io.PipedInputStream;
import java.net.Socket;

import sysu.java.client.host.SocketTask;

public class ClientUI {
    private Socket client;

    public ClientUI(Socket client) {
        this.client = client;
    }
}
