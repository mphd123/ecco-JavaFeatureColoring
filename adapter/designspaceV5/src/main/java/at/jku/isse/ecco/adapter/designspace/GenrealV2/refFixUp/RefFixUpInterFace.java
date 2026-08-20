package at.jku.isse.ecco.adapter.designspace.GenrealV2.refFixUp;

import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;

import java.util.Map;
import java.util.Set;

public interface RefFixUpInterFace {
    void fixUp(Workspace workspace, Set<WorkspaceElement> elements);

    WorkspaceElement getOwningElement();
}
