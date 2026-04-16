package at.jku.isse.ecco.adapter.designspace.exception;

public class IDMapperException extends UnsupportedOperationException {

    private static final String EXCEPTION_MESSAGE = " THe provided ID Mapper is invalid because : ";

    public IDMapperException(String reason) {
        super(EXCEPTION_MESSAGE + reason);
    }
}

