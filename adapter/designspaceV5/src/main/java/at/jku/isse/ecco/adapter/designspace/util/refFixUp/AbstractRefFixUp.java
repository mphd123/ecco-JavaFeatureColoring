package at.jku.isse.ecco.adapter.designspace.util.refFixUp;


import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;



public abstract class AbstractRefFixUp implements RefFixUpInterFace {
    public final WorkspaceElement instance;
    public final WorkspacePropertyType propertyType;

    public AbstractRefFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType) {
        this.instance = instance;
        this.propertyType = propertyType;
    }

    public WorkspaceElement getInstance() {return instance;}

}
