package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;

import java.util.Map;

public interface RefFixUpInterFace {
    void fixUp(Workspace workspace, Map<Long,Long> newToOriginalId);
    WorkspaceElement getInstance();
}
