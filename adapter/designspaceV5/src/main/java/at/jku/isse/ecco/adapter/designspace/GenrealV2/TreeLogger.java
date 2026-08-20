package at.jku.isse.ecco.adapter.designspace.GenrealV2;

import at.jku.isse.ecco.adapter.designspace.util.DebugOptions;

public class TreeLogger {
    private static final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    public static DebugOptions debugOptions;

    public static Scope enter(String message) {
        log(message);
        depth.set(depth.get() + 1);
        return () -> depth.set(Math.max(0, depth.get() - 1));
    }

    public static void log(String message) {
        if (debugOptions.javaConsole()) {
            int d = depth.get();
            if (d == 0) {
                System.out.println("┌── " + message);
            } else {
                String indent = "│   ".repeat(d - 1);
                System.out.println(indent + "├── " + message);
            }
        }
    }


    public static void reset() {
        depth.set(0);
    }

    @FunctionalInterface
    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }
}