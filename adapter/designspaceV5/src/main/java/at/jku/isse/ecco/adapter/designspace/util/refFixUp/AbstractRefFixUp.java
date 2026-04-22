package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.Optional;

public abstract class AbstractRefFixUp implements RefFixUpInterFace {
    public final Instance instance;
    public final PropertyType propertyType;

    public AbstractRefFixUp(Instance instance, PropertyType propertyType) {
        this.instance = instance;
        this.propertyType = propertyType;
    }

}
