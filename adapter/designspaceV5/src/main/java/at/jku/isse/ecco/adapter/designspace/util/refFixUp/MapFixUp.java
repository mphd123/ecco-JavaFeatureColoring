package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.foundation.Key;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapFixUp extends AbstractRefFixUp{
    Map<Key,Long> refMap;

    public MapFixUp(Instance instance, PropertyType propertyType, Map<Key, Long> refMap) {
        super(instance, propertyType);
        this.refMap = refMap;
    }

    @Override
    public void fixUp(Workspace workspace, Map<Long, Long> newToOriginalId) {
        Map<Key,Object> instanceMap = new HashMap<>();
        refMap.forEach((key, refID) -> {
            Optional<Instance> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).getClosestInstance(instance.getFolder());
            if (refInstance.isEmpty()) throw new RuntimeException("could not find instance for refId");
            instance.add(propertyType,key,refInstance.get());
        });
    }
}
