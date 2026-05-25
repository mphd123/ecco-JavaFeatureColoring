package at.jku.isse.ecco.adapter.designspace.util;


import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;

public record RefProeprtyFixupRecord(WorkspaceElement instance, WorkspacePropertyType propertyType, Long refElementID) {
}
