package sysu.java.server.host;

import sysu.java.IPanHost;
import sysu.java.IPanHostBuilder;

public class ServerPanHostBuilder implements IPanHostBuilder {
    private int port = 3000;
    private String basePath = "data";

    @Override
    public IPanHost build() {
        return new ServerPanHost(port, basePath);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
