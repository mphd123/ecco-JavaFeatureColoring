package at.jku.isse.ecco.adapter.designspace.util.refFixUp;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.*;

public class CollectionFixUp extends AbstractRefFixUp {
    public final Collection<Long> refCollection;

    public CollectionFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType, Collection<Long> refCollection) {
        super(instance, propertyType);
        this.refCollection = refCollection;
    }

    @Override
    public void fixUp(Workspace workspace, Map<Long, Long> newToOriginalId) {
        Collection<WorkspaceElement> refInstances;

        if (propertyType.getCardinality().equals(Cardinality.UNORDERED_SET)) refInstances = new HashSet<>();
        else if (propertyType.getCardinality().equals(Cardinality.LIST) || propertyType.getCardinality().equals(Cardinality.ORDERED_SET))
            refInstances = new ArrayList<>();
        else throw new RuntimeException("CollectionFixUp received invalid Cardinality");
        StringBuilder debuginfo = new StringBuilder("RefFixupCollection[type = " + propertyType.getName() + " / cad " + propertyType.cardinality + " ]").append("\n");


        for (Long refID : refCollection) {
            Optional<WorkspaceElement> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).search(instance.getFolder());
            debuginfo.append("orignal: ").append(refID).append("found ").append(refInstance.get());
            if (refInstance.isEmpty()) throw new RuntimeException("could not find instance for refId");
            refInstances.add(refInstance.get());
        }
        Logger.log(debuginfo.toString(), instance);
        instance.setAll(propertyType, refInstances);
    }
}
