package sysu.java.server.host.actions;

import sysu.java.server.host.ServerCommands;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.DownloadFile)
public class DownloadFileAction extends Action {
    @Override
    public void execute(int length, InputStream input) {
        try {
            var path = Paths.get(host.getBasePath(), new String(input.readNBytes(length), StandardCharsets.UTF_8)).toAbsolutePath();
            var file = path.toFile();
            if (!file.exists()) {
                writeFailure("File with the name doesn't exist");
                return;
            }
            var fileLen = (int) file.length();
            writeHeader(fileLen);
            var fileStream = new FileInputStream(file);
            var outputStream = new PipedOutputStream();
            new Thread(() -> {
                try {
                    writeBody(new PipedInputStream(outputStream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            var buffer = new byte[1048576];
            var cnt = 0;
            while (cnt < fileLen) {
                var len = input.read(buffer);
                outputStream.write(buffer, 0, len);
                cnt += len;
            }
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
