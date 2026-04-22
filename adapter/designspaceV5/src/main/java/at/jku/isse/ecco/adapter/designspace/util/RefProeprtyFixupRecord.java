package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.PropertyType;

public record RefProeprtyFixupRecord(Instance instance, PropertyType propertyType, Long refElementID) {
}
