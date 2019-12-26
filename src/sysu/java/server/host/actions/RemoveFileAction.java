package sysu.java.server.host.actions;

import sysu.java.server.host.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.RemoveFile)
public class RemoveFileAction extends Action {

    @Override
    public void execute(int length, InputStream input) {
        try {
            var path = Paths.get(host.getBasePath(), new String(input.readNBytes(length), StandardCharsets.UTF_8)).toAbsolutePath();
            var file = path.toFile();
            if (file.isFile() && file.exists() && file.delete()) {
                writeSuccess("File removed successfully");
            } else {
                writeSuccess("File removed failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
