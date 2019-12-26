package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.RemoveFolder)
public class RemoveFolderAction extends Action {
    @Override
    public void execute(int length, InputStream input) {
        try {
            var path = Paths.get(host.getBasePath(), new String(input.readNBytes(length), StandardCharsets.UTF_8)).toAbsolutePath();
            var file = path.toFile();
            if (file.isDirectory() && file.exists()) {
                Utils.deleteDirectory(file);
                writeSuccess("Folder removed successfully");
            } else {
                writeSuccess("Folder removed failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
