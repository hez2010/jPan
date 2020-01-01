package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerPanHost;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class Action {
    protected ServerPanHost host;
    private int id;
    private OutputStream output;
    private int length = -1;
    private boolean wrote = false;

    public boolean invoke(ServerPanHost host, int id, int length, InputStream input, OutputStream output) {
        this.id = id;
        this.output = output;
        this.host = host;
        return execute(length, input);
    }

    public abstract boolean execute(int length, InputStream input);

    protected void writeHeader(int length, int status) throws IOException {
        if (wrote) throw new RuntimeException("A message has already been written");
        this.length = length;
        output.write(Utils.intToByte4(id));
        output.write(Utils.intToByte4(length));
        output.write(Utils.intToByte4(status));
        wrote = true;
    }

    protected void writeSuccess(String message) throws IOException {
        try {
            var buf = new ByteArrayOutputStream();
            buf.write("success".getBytes(StandardCharsets.UTF_8));
            buf.write(Utils.messageSplitter);
            buf.write(message.getBytes(StandardCharsets.UTF_8));
            writeHeader(buf.size(), 0);
            writeBody(buf.toByteArray(), 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void writeFailure(String message) throws IOException {
        try {
            var buf = new ByteArrayOutputStream();
            buf.write("failure".getBytes(StandardCharsets.UTF_8));
            buf.write(Utils.messageSplitter);
            buf.write(message.getBytes(StandardCharsets.UTF_8));
            writeHeader(buf.size(), 1);
            writeBody(buf.toByteArray(), 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void writeBody(PipedInputStream input) throws IOException {
        if (length == -1) throw new RuntimeException("No header has been written");
        Utils.transferWithLength(length, input, output);
        input.close();
    }

    protected void writeBody(byte[] input, int off) throws IOException {
        if (length == -1) throw new RuntimeException("No header has been written");
        output.write(input, off, length);
    }
}
