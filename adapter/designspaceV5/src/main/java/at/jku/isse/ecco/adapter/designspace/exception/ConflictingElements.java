package at.jku.isse.ecco.adapter.designspace.exception;

import at.jku.isse.designspace.core.model.WorkspaceElement;

import java.util.List;
import java.util.Set;

public class ConflictingElements extends RuntimeException{

    public final List<Set<WorkspaceElement>> conflictingElements;



    public ConflictingElements(List<Set<WorkspaceElement>> conflictingElements) {
        super("Some Elements share their Name");
        this.conflictingElements = conflictingElements;
    }
}
