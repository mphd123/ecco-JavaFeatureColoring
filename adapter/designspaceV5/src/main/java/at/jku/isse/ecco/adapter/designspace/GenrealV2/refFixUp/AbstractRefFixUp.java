package at.jku.isse.ecco.adapter.designspace.GenrealV2.refFixUp;


import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;


public abstract class AbstractRefFixUp implements RefFixUpInterFace {
    public final WorkspaceElement owningElement;
    public final WorkspacePropertyType propertyType;

    public AbstractRefFixUp(WorkspaceElement owningElement, WorkspacePropertyType propertyType) {
        this.owningElement = owningElement;
        this.propertyType = propertyType;
    }

    public WorkspaceElement getOwningElement() {
        return owningElement;
    }

}
