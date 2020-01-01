package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.DownloadFile)
public class DownloadFileAction extends Action {
    @Override
    public boolean execute(int length, InputStream input) {
        try {
            var pathStr = new String(input.readNBytes(length), StandardCharsets.UTF_8);
            if (!Utils.checkPath(pathStr)) {
                writeFailure("Illegal path");
                return false;
            }
            var path = Paths.get(host.getBasePath(), pathStr).toAbsolutePath();
            var file = path.toFile();
            if (!file.exists()) {
                writeFailure("File with the name doesn't exist");
                return false;
            }
            var fileLen = (int) file.length();
            writeHeader(fileLen, 0);
            var fileStream = new FileInputStream(file);
            var outputStream = new PipedOutputStream();
            var inputStream = new PipedInputStream(outputStream);
            new Thread(() -> {
                try {
                    writeBody(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            Utils.transferWithLength(fileLen, fileStream, outputStream);
            fileStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
