package sysu.java;

import sysu.java.client.host.ClientPanHostBuilder;
import sysu.java.server.host.ServerPanHostBuilder;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length <= 0) args = new String[]{"--server"};

        IPanHostBuilder builder = null;

        var mode = args[0];
        if (Objects.equals(mode, "--server")) {
            var serverBuilder = new ServerPanHostBuilder();
            serverBuilder.setPort(3000);
            serverBuilder.setBasePath("data");
            builder = serverBuilder;
        }
        if (Objects.equals(mode, "--client")) {
            var clientBuilder = new ClientPanHostBuilder();
            clientBuilder.setServerAddress("localhost");
            clientBuilder.setServerPort(3000);
            builder = clientBuilder;
        }

        if (builder == null) throw new IllegalArgumentException("Illegal running arguments");
        builder.build().run();
    }
}
