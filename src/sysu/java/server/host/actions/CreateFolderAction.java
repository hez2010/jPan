package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.CreateFolder)
public class CreateFolderAction extends Action {
    @Override
    public boolean execute(int length, InputStream input) {
        try {
            var pathStr = new String(input.readNBytes(length), StandardCharsets.UTF_8);
            if (!Utils.checkPath(pathStr)) {
                writeFailure("路径非法");
                return false;
            }
            var path = Paths.get(host.getBasePath(), pathStr).toAbsolutePath();
            var file = path.toFile();
            if (!file.exists() && file.mkdir()) {
                writeSuccess("文件夹创建成功");
                return true;
            } else {
                writeFailure("文件夹创建失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
