package at.jku.isse.ecco.adapter.designspace.util.refFixUp;

import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.Optional;

public abstract class AbstractRefFixUp implements RefFixUpInterFace {
    public final WorkspaceElement instance;
    public final WorkspacePropertyType propertyType;

    public AbstractRefFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType) {
        this.instance = instance;
        this.propertyType = propertyType;
    }

}
