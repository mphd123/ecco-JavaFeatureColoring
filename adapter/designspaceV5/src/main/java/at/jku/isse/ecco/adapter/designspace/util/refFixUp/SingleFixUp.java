package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.Map;
import java.util.Optional;

public class SingleFixUp extends AbstractRefFixUp{
    public final Long refID;

    public SingleFixUp(Instance instance, PropertyType propertyType, Long refID) {
        super(instance, propertyType);
        this.refID = refID;
    }


    @Override
    public void fixUp(Workspace workspace, Map<Long,Long> newToOriginalId) {
        Optional<Instance> refInstance = new RefIdSearcher(workspace, refID, newToOriginalId).getClosestInstance(instance.getFolder());
        if (refInstance.isEmpty()) throw new RuntimeException();
        instance.set(propertyType,refInstance.get());

    }
}
