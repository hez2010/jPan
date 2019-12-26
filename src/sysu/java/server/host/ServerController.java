package sysu.java.server.host;

import io.github.classgraph.ClassGraph;
import sysu.java.server.host.actions.Action;
import sysu.java.server.host.actions.RegisterAction;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ServerController {
    private final HashMap<Integer, Action> map = new HashMap<>();
    private ServerPanHost host;

    public void registerActions(ServerPanHost host) {
        this.host = host;
        try (var result = new ClassGraph()
                .enableAllInfo()
                .whitelistPackages("sysu.java.server.host")
                .scan()) {
            var classes = result.getAllClasses();
            for (var i : classes) {
                var annotation = i.getAnnotationInfo("sysu.java.server.host.actions.RegisterAction");
                if (annotation != null) {
                    map.put(((RegisterAction) annotation.loadClassAndInstantiate()).command().ordinal(),
                            (Action) (i.loadClass().getConstructor().newInstance()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeAction(ServerCommands command, int id, int length, InputStream input, OutputStream output) {
        var key = command.ordinal();
        var action = map.get(key);
        if (action != null) action.invoke(host, id, length, input, output);
    }
}
