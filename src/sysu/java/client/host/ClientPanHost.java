package sysu.java.client.host;

import sysu.java.IPanHost;
import sysu.java.client.gui.ClientUI;

import java.io.IOException;
import java.net.Socket;

public class ClientPanHost implements IPanHost {
    private String address;
    private int port;

    public ClientPanHost(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() throws IOException {
        var socket = new Socket(address, port);
        new ClientUI(socket);
    }
}
