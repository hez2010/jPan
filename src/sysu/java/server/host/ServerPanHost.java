package sysu.java.server.host;

import sysu.java.IPanHost;
import sysu.java.Utils;

import java.io.File;
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
        var dir = new File(basePath);
        if (!dir.isDirectory() && !dir.exists())
            dir.mkdirs();
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
                var addr = client.getRemoteSocketAddress().toString();
                System.out.println("Client " + addr + " connected");
                new Thread(() -> clientHandler(client)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clientHandler(Socket client) {
        var addr = client.getRemoteSocketAddress().toString();
        while (!client.isClosed()) {
            try {
                var input = client.getInputStream();
                var output = client.getOutputStream();
                var id = Utils.byte4ToInt(input.readNBytes(4), 0);
                var length = Utils.byte4ToInt(input.readNBytes(4), 0);
                var command = Utils.byte4ToInt(input.readNBytes(4), 0);
                System.out.println("Command " + ServerCommands.values()[command] +
                        " from client " + addr + " received: \n  --> Params: id = " + id + ", length = " + length);
                controller.executeAction(ServerCommands.values()[command], id, length - 4, input, output);
            } catch (IOException e) {
                System.out.println("Client " + addr + " left");
                break;
            }
        }
    }

    public String getBasePath() {
        return basePath;
    }
}
