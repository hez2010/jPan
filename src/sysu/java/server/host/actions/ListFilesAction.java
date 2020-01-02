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
    public boolean execute(int length, InputStream input) {
        try {
            var pathStr = new String(input.readNBytes(length), StandardCharsets.UTF_8);
            if (!Utils.checkPath(pathStr)) {
                writeFailure("路径非法");
                return false;
            }
            var path = Paths.get(host.getBasePath(), pathStr).toAbsolutePath();
            var fileList = Utils.listFiles(path.toString());
            var buffer = new ByteArrayOutputStream();

            for (var file : fileList) {
                var fileInfo = file.getBytes(StandardCharsets.UTF_8);
                buffer.write(fileInfo);
                buffer.write(Utils.messageSplitter);
            }

            writeHeader(buffer.size(), 0);
            writeBody(buffer.toByteArray(), 0);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
