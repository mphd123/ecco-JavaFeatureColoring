package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;

import java.util.Map;

public record DesignSpaceInfo(Workspace workspace, Folder folder, Map<Long,Long> newToOriginalIdMap) {
}
