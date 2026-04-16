package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.ecco.IdMapper;

import java.util.Map;

public record DesignSpaceInfo(Workspace workspace, Folder folder, IdMapper idMapper) {
}
