package sysu.java.client.host;

import sysu.java.Header;
import sysu.java.Utils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.HashMap;

enum TaskStatus {
    Sending, Receiving, Completed
}

public abstract class SocketTask {
    private static final HashMap<Integer, SocketTask> map = new HashMap<>();
    private TaskStatus status = TaskStatus.Sending;

    public void postTask(Socket client, int length, PipedInputStream input) throws IOException {
        var header = new Header();
        header.setLength(length);
        var id = header.getId();
        synchronized (map) {
            map.put(id, this);
        }
        new Thread(() -> handleMessage(client)).start();
        new Thread(() -> {
            try {
                var output = client.getOutputStream();
                output.write(Utils.intToByte4(id));
                output.write(Utils.intToByte4(length));
                var count = 0;
                while (count < length) {
                    var available = Math.min(input.available(), Math.max(length - count, 0));
                    var buffer = input.readNBytes(available);
                    count += buffer.length;
                    output.write(buffer);
                }
                input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public TaskStatus getStatus() {
        return status;
    }

    private void handleBody(int length, Socket client) throws IOException {
        status = TaskStatus.Receiving;
        var output = new PipedOutputStream();
        var stream = new PipedInputStream(output);
        new Thread(() -> completedTask(length, stream)).start();
        var count = 0;
        var input = client.getInputStream();
        while (count < length) {
            var available = Math.min(input.available(), Math.max(length - count, 0));
            var buffer = input.readNBytes(available);
            count += buffer.length;
            output.write(buffer);
        }
        status = TaskStatus.Completed;
    }

    private void handleMessage(Socket client) {
        try {
            var input = client.getInputStream();
            var incomingId = Utils.byte4ToInt(input.readNBytes(4), 0);
            var length = Utils.byte4ToInt(input.readNBytes(4), 0);
            SocketTask handler;
            synchronized (map) {
                handler = map.get(incomingId);
                map.remove(incomingId);
            }
            if (handler != null) {
                handler.handleBody(length, client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void completedTask(int length, PipedInputStream input);
}
