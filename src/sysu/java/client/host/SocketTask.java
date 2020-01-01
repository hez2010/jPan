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
    private static final Object lock = new Object();

    private synchronized static void handleMessage(Socket client) {
        try {
            var input = client.getInputStream();
            var incomingId = Utils.byte4ToInt(input.readNBytes(4), 0);
            var length = Utils.byte4ToInt(input.readNBytes(4), 0);
            var flagStatus = Utils.byte4ToInt(input.readNBytes(4), 0);
            SocketTask handler;
            synchronized (map) {
                handler = map.get(incomingId);
                map.remove(incomingId);
            }
            if (handler != null) {
                handler.handleBody(length, flagStatus, client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postTask(Socket client, byte[] input) {
        var header = new Header();
        header.setLength(input.length);
        var id = header.getId();
        synchronized (map) {
            map.put(id, this);
        }
        new Thread(() -> handleMessage(client)).start();
        new Thread(() -> {
            synchronized (lock) {
                try {
                    var output = client.getOutputStream();
                    output.write(Utils.intToByte4(id));
                    output.write(Utils.intToByte4(input.length));
                    output.write(input);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void postTask(Socket client, int length, PipedInputStream input) {
        var header = new Header();
        header.setLength(length);
        var id = header.getId();
        synchronized (map) {
            map.put(id, this);
        }

        new Thread(() -> handleMessage(client)).start();
        new Thread(() -> {
            synchronized (lock) {
                try {
                    var output = client.getOutputStream();
                    output.write(Utils.intToByte4(id));
                    output.write(Utils.intToByte4(length));
                    Utils.transferWithLength(length, input, output);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        input.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void handleBody(int length, int flagStatus, Socket client) throws IOException {
        status = TaskStatus.Receiving;
        var output = new PipedOutputStream();
        var stream = new PipedInputStream(output);
        new Thread(() -> completedTask(length, flagStatus, stream)).start();
        var input = client.getInputStream();
        Utils.transferWithLength(length, input, output);
        status = TaskStatus.Completed;
    }

    public abstract void completedTask(int length, int status, PipedInputStream input);
}
