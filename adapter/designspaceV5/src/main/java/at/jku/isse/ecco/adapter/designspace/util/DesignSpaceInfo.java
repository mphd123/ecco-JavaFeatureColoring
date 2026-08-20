package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;

import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.EccoException;
import at.jku.isse.ecco.adapter.designspace.exception.FolderException;
import at.jku.isse.ecco.adapter.designspace.exception.IDMapperException;
import at.jku.isse.ecco.adapter.designspace.exception.WorkspaceException;

import java.util.Collection;

public record DesignSpaceInfo(Workspace workspace, Folder folder, IdMapper idMapper, DebugOptions debugOptions,
                              adapterType adapterType) {
    public void checkIfInfoValid() {
        if (idMapper() == null) throw new IDMapperException("is null");
        if (idMapper().getCurrentRepId() == null || idMapper().getCurrentRepId().isBlank()) {
            throw new IDMapperException(String.format("the set repId for IDMapper is invalid is [%s]", idMapper().getCurrentRepId()));
        }
        if (workspace() == null) throw new WorkspaceException("is null");
        if (folder() == null) throw new FolderException("is null");


        if (debugOptions == null) throw new EccoException(" debug Options are null");
        if (adapterType == null) throw new EccoException(" writerType is null");
    }

    public void checkIfFolderIsReadyForCheckout() {
        Collection<WorkspaceElement> instances = (Collection<WorkspaceElement>) folder().getWorkspaceElementContents(workspace);
        if (!instances.isEmpty() || !folder().getSubFolders().isEmpty())
            throw new FolderException("the chosen Folder is not empty");

    }

    public enum adapterType {
        GENERAL,
        GeneralV2,
        JAVA

    }
}
