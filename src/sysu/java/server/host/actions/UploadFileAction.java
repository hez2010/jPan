package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.UploadFile)
public class UploadFileAction extends Action {
    @Override
    public boolean execute(int length, InputStream input) {
        try {
            OutputStream fileStream = null;
            var pathBuf = Utils.seekSplitter(input);
            var pathStr = new String(pathBuf, StandardCharsets.UTF_8);
            if (!Utils.checkPath(pathStr)) {
                fileStream = new ByteArrayOutputStream();
                writeFailure("Illegal path");
            }
            var path = Paths.get(host.getBasePath(), pathStr).toAbsolutePath();
            var file = path.toFile();
            if (fileStream == null) {
                if (file.exists()) {
                    fileStream = new ByteArrayOutputStream();
                    writeFailure("File with the same name already exists");
                } else {
                    try {
                        fileStream = new FileOutputStream(file);
                    } catch (Exception ex) {
                        fileStream = new ByteArrayOutputStream();
                        writeFailure("Folder doesn't exist");
                    }
                }
            }
            var remain = length - pathBuf.length - Utils.messageSplitter.length;
            try {
                Utils.transferWithLength(remain, input, fileStream);
                if (fileStream instanceof FileOutputStream) {
                    writeSuccess("Successfully uploaded file");
                    return true;
                }
            } finally {
                fileStream.close();
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
