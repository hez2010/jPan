package sysu.java.client.host;

import sysu.java.IPanHost;
import sysu.java.IPanHostBuilder;

public class ClientPanHostBuilder implements IPanHostBuilder {
    private String serverAddress;
    private int serverPort;

    @Override
    public IPanHost build() {
        return new ClientPanHost(serverAddress, serverPort);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
