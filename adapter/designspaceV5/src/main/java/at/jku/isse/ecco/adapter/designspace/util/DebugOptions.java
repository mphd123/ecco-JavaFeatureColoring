package at.jku.isse.ecco.adapter.designspace.util;

public record DebugOptions(boolean generalAdapterConsole, boolean javaConsole, boolean javaLogFile) {

    public static DebugOptions getNoDebugOptions() {
        return new DebugOptions(false, false, false);
    }

    public static DebugOptions getFullDebugOptions() {
        return new DebugOptions(true, true, true);
    }
}
