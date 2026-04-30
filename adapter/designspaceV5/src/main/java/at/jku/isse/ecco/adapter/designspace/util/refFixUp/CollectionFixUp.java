package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.*;

public class CollectionFixUp extends AbstractRefFixUp{
    public final Collection<Long> refCollection;

    public CollectionFixUp(Instance instance, PropertyType propertyType, Collection<Long> refCollection) {
        super(instance, propertyType);
        this.refCollection = refCollection;
    }

    @Override
    public void fixUp(Workspace workspace, Map<Long, Long> newToOriginalId) {
        Collection<Instance> refInstances;

        if(propertyType.getCardinality().equals(Cardinality.SET)) refInstances = new HashSet<>();
        else if (propertyType.getCardinality().equals(Cardinality.LIST) || propertyType.getCardinality().equals(Cardinality.ORDERED_SET)) refInstances = new ArrayList<>();
        else throw new RuntimeException("CollectionFixUp received invalid Cardinality");

        for (Long refID : refCollection) {
            Optional<Instance> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).getClosestInstance(instance.getFolder());
            if (refInstance.isEmpty()) throw new RuntimeException("could not find instance for refId");
            refInstances.add(refInstance.get());
        }
        instance.setAll(propertyType,refInstances);
    }
}
