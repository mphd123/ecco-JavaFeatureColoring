package at.jku.isse.ecco.adapter.designspace.exception;

public class FolderException extends UnsupportedOperationException {

    private static final String EXCEPTION_MESSAGE = " THe provided Folder is invalid because : ";

    public  FolderException(String reason) {
        super(EXCEPTION_MESSAGE);
    }
}