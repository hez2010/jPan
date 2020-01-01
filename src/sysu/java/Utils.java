package sysu.java;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Utils {
    public static final byte[] messageSplitter;

    static {
        messageSplitter = "!*jPan*splitter!".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    public static void deleteDirectory(File directoryToBeDeleted) {
        var allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (var file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    public static String[] getResult(InputStream stream, int length) throws IOException {
        var result = seekSplitter(stream);
        length -= result.length + messageSplitter.length;
        String message;
        if (length > 0)
            message = new String(stream.readNBytes(length), StandardCharsets.UTF_8);
        else message = "";
        return new String[]{new String(result, StandardCharsets.UTF_8), message};
    }

    public static String[] listFiles(String path) {
        var dirFile = new File(path);
        var result = new ArrayList<String>();
        if (dirFile.isDirectory()) {
            try {
                var dir = Files.newDirectoryStream(Path.of(path));
                for (var i : dir) {
                    var file = i.toFile();
                    if (file.isDirectory())
                        result.add("d:" + 0 + ":" + file.getName());
                    else
                        result.add("f:" + file.length() + ":" + file.getName());
                }
                dir.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return result.toArray(new String[0]);
    }

    public static void transferWithLength(int length, InputStream input, OutputStream output) throws IOException {
        var buffer = new byte[131072];
        var count = 0;
        while (count < length) {
            if (length - count >= 131072) {
                var len = input.read(buffer);
                count += len;
                output.write(buffer, 0, len);
            } else {
                var remainBuffer = input.readNBytes(length - count);
                count += remainBuffer.length;
                output.write(remainBuffer);
            }
        }
    }

    public static boolean checkPath(String path) {
        var tmp = path.replaceAll("\\\\", "/");
        return !tmp.equals("..") && !tmp.contains("../") && !tmp.contains("./") && !tmp.equals(".");
    }

    private static boolean compare(byte[] buffer, int offset) {
        if ((buffer.length - offset) != messageSplitter.length) return false;
        for (var i = 0; i < messageSplitter.length; i++) {
            if (buffer[offset + i] != messageSplitter[i]) return false;
        }
        return true;
    }

    public static byte[] seekSplitter(InputStream stream) throws IOException {
        var buffer = new ByteArrayOutputStream();
        if (stream.available() == 0) return buffer.toByteArray();
        buffer.write(stream.readNBytes(messageSplitter.length));
        var off = 0;
        var array = buffer.toByteArray();
        while (!compare(array, off)) {
            off++;
            buffer.write(stream.read());
            array = buffer.toByteArray();
        }
        var retBuffer = new ByteArrayOutputStream();
        retBuffer.write(array, 0, array.length - messageSplitter.length);
        return retBuffer.toByteArray();
    }
}