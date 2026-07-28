package at.jku.isse.ecco.adapter.designspace.util.refFixUp;


import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;

import java.util.Map;
import java.util.Optional;

public class SingleFixUp extends AbstractRefFixUp {
    public final Long refID;

    public SingleFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType, Long refID) {
        super(instance, propertyType);
        this.refID = refID;
    }


    @Override
    public void fixUp(Workspace workspace, Map<Long, Long> newToOriginalId) {

        Optional<WorkspaceElement> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).search(instance.getFolder());
        if (refInstance.isEmpty()) throw new RuntimeException();
        Logger.log("RefFixupSingle[type = " + propertyType.getName() + " ]for originalID= " + refID + " Element with id" + refInstance.get().getId() + "was found", instance);

        // with opposed properties when setting theirs it can cause all opposed to be set on it and i think it happens when it has already been set
        if (instance.get(propertyType) != null) {
            Logger.log("Instance " + instance + "for property" + propertyType + " already has a value assumed from a different opposing property value was " + instance.get(propertyType));
        }
        ;
        instance.set(propertyType, refInstance.get());
        Logger.log("detail after set \n" + WriterTypeManager.detailRepresentation(instance));


    }
}
