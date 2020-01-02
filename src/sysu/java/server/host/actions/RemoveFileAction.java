package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.RemoveFile)
public class RemoveFileAction extends Action {

    @Override
    public boolean execute(int length, InputStream input) {
        try {
            var pathStr = new String(input.readNBytes(length), StandardCharsets.UTF_8);
            if (!Utils.checkPath(pathStr) || pathStr.isBlank()) {
                writeFailure("路径非法");
                return false;
            }
            var path = Paths.get(host.getBasePath(), pathStr).toAbsolutePath();
            var file = path.toFile();
            if (file.isFile() && file.exists() && file.delete()) {
                writeSuccess("文件删除成功");
                return true;
            } else {
                writeSuccess("文件删除失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
