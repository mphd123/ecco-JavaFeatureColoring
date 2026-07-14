package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.WorkspaceElement;

public class Logger {
    public static boolean enabled = false;
    public static void log(String message, WorkspaceElement instance) {
        if (!enabled)return;
        if(instance==null) {
            System.out.println("Debug "  + message);
        }else if (isToBeLoggedType(instance)) {
            System.out.println("Debug instance= " + instance.getName() + " with type"  +instance.getInstanceOf() +"\n -------------------------------------------------------------------- " + message + "\n");
        }
    }

    public static boolean isToBeLoggedType(WorkspaceElement instance) {
        return instance.getInstanceOf().getName().toLowerCase().contains("for") ||instance.getInstanceOf().getName().toLowerCase().contains("if");
    }


    public static void log(String message) {
        log(message, null);
    }
}
