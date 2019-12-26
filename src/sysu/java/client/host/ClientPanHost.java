package sysu.java.client.host;

import sysu.java.IPanHost;

import java.io.IOException;
import java.net.Socket;

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
    }
}
