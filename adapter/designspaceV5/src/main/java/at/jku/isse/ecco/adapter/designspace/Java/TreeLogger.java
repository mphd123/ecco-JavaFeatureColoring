package at.jku.isse.ecco.adapter.designspace.Java;

import at.jku.isse.ecco.adapter.designspace.util.DebugOptions;

import java.util.function.Supplier;

public class TreeLogger {
    private static final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    private static final Scope NO_OP = () -> {};
    public static DebugOptions debugOptions;


    public static boolean isLoggingEnabled() {
        return debugOptions != null && debugOptions.javaConsole();
    }

    public static Scope enter(Supplier<String> messageSupplier) {
        if (!isLoggingEnabled()) {
            return NO_OP;
        }

        log(messageSupplier.get());
        depth.set(depth.get() + 1);
        return () -> depth.set(Math.max(0, depth.get() - 1));
    }

    public static Scope enter(String message) {
        if (!isLoggingEnabled()) {
            return NO_OP;
        }

        log(message);
        depth.set(depth.get() + 1);
        return () -> depth.set(Math.max(0, depth.get() - 1));
    }

    public static void log(String message) {
        if (!isLoggingEnabled()) return;

        int d = depth.get();
        if (d == 0) {
            System.out.println("┌── " + message);
        } else {
            String indent = "│   ".repeat(d - 1);
            System.out.println(indent + "├── " + message);
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