package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;

import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.designspace.exception.FolderException;
import at.jku.isse.ecco.adapter.designspace.exception.IDMapperException;
import at.jku.isse.ecco.adapter.designspace.exception.WorkspaceException;

import java.util.Collection;

public record DesignSpaceInfo(Workspace workspace, Folder folder, IdMapper idMapper,boolean printDebug, writerType writerType) {
    public void checkIfInfoValid(DesignSpaceInfo info) {
        if (info.idMapper() == null) throw new IDMapperException("is null");
        if (info.idMapper().getCurrentRepId() == null || info.idMapper().getCurrentRepId().isBlank()) {
            throw new IDMapperException(String.format("the set repId for IDMapper is invalid is [%s]",info.idMapper().getCurrentRepId()));
        }
        if (info.workspace() == null) throw new WorkspaceException("is null");
        if (info.folder() == null) throw new FolderException("is null");
        Collection<WorkspaceElement> instances = (Collection<WorkspaceElement>) info.folder().getWorkspaceElementContents(workspace);
        if(!instances.isEmpty() || !info.folder().getSubFolders().isEmpty()) throw new FolderException("the chosen Folder is not empty");
    }

    public static enum writerType {
        GENERAL,
        JAVA

    }
}
