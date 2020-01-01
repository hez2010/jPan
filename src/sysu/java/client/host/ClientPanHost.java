package sysu.java.client.host;

import sysu.java.IPanHost;
import sysu.java.Utils;
import sysu.java.client.gui.ClientUI;
import sysu.java.server.host.ServerCommands;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientPanHost implements IPanHost {
    private String address;
    private int port;
    private Socket socket = null;

    public ClientPanHost(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() throws IOException {
        socket = new Socket(address, port);
        new ClientUI(socket);
    }
}
