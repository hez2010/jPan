package sysu.java.server.host.actions;

import sysu.java.Utils;
import sysu.java.server.host.ServerCommands;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RegisterAction(command = ServerCommands.UploadFile)
public class UploadFileAction extends Action {
    @Override
    public void execute(int length, InputStream input) {
        try {
            var path = Paths.get(host.getBasePath(), new String(Utils.seekSplitter(input), StandardCharsets.UTF_8)).toAbsolutePath();
            var file = path.toFile();
            if (file.exists()) {
                writeFailure("File with the same name already exists");
                return;
            }
            var fileStream = new FileOutputStream(file);
            var buffer = new byte[1048576];
            var cnt = 0;
            while (cnt < length) {
                var len = input.read(buffer);
                fileStream.write(buffer, 0, len);
                cnt += len;
            }
            fileStream.close();
            writeSuccess("Successfully uploaded file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
