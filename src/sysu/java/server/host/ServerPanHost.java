package sysu.java.server.host;

import sysu.java.IPanHost;
import sysu.java.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPanHost implements IPanHost {
    private final String basePath;
    private int port;
    private ServerSocket server;
    private ServerController controller = new ServerController();

    public ServerPanHost(int port, String basePath) {
        this.port = port;
        this.basePath = basePath;
    }

    @Override
    public void run() {
        controller.registerActions(this);
        try {
            server = new ServerSocket(port);
            new Thread(this::acceptClient).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptClient() {
        while (!server.isClosed()) {
            try {
                var client = server.accept();
                new Thread(() -> clientHandler(client)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clientHandler(Socket client) {
        while (!client.isClosed()) {
            try {
                var input = client.getInputStream();
                var output = client.getOutputStream();
                var id = Utils.byte4ToInt(input.readNBytes(4), 0);
                var length = Utils.byte4ToInt(input.readNBytes(4), 0);
                var command = Utils.byte4ToInt(input.readNBytes(4), 0);
                controller.executeAction(ServerCommands.values()[command], id, length - 4, input, output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBasePath() {
        return basePath;
    }
}
