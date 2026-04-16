package at.jku.isse.ecco.adapter.designspace.exception;

public class WorkspaceException extends IllegalArgumentException{

    private static final String EXCEPTION_MESSAGE = "THe provided workspace is invalid because ";

    public WorkspaceException(String reason) {
        super(EXCEPTION_MESSAGE + reason);
    }
}
