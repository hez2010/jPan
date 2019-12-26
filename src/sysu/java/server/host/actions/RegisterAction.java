package sysu.java.server.host.actions;

import sysu.java.server.host.ServerCommands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterAction {
    ServerCommands command();
}
