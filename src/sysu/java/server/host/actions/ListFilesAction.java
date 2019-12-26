package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.ListFiles)
public class ListFilesAction extends Action {
    @Override
    public void execute(int length, InputStream input) {
        try {
            var path = Paths.get(host.getBasePath(), new String(input.readNBytes(length), StandardCharsets.UTF_8)).toAbsolutePath();
            var fileList = Utils.listFiles(path.toString());
            var buffer = new ByteArrayOutputStream();

            for (var file : fileList) {
                var fileInfo = file.getBytes(StandardCharsets.UTF_8);
                buffer.write(fileInfo);
                buffer.write(Utils.messageSplitter);
            }

            writeHeader(buffer.size());
            writeBody(buffer.toByteArray(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
