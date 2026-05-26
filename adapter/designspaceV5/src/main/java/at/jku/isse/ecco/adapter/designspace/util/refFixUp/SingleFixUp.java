package at.jku.isse.ecco.adapter.designspace.util.refFixUp;


import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.Map;
import java.util.Optional;

public class SingleFixUp extends AbstractRefFixUp{
    public final Long refID;

    public SingleFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType, Long refID) {
        super(instance, propertyType);
        this.refID = refID;
    }


    @Override
    public void fixUp(Workspace workspace, Map<Long,Long> newToOriginalId) {
        Optional<WorkspaceElement> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).getClosestInstance(instance.getFolder());
        if (refInstance.isEmpty()) throw new RuntimeException();
        instance.set(propertyType,refInstance.get());

    }
}
