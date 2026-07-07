package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class FolderArtefact extends CommitFolderArtefact {
    private final String name;
    private final Long id;
    public FolderArtefact(String name,Long id) {
        this.name = name;
        this.id = id;
    }



    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public void buildFolder(Workspace workspace, Folder parentFolder, Node folderNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
            Folder folder = DesignSpace.createFolder(name,parentFolder);
            writerTypeManager.newToOriginalId.put(folder.getId(),id);
            for (Node child : folderNode.getChildren()) {
                if (child.getArtifact().getData() instanceof FolderArtefact subFolder) {
                    subFolder.buildFolder(workspace,folder,child,writerTypeManager);
                }else if ( child.getArtifact().getData() instanceof InstanceTypeArtefact instanceTypeArtefact) {
                    instanceTypeArtefact.build(workspace,folder,child,writerTypeManager);
                }
            }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FolderArtefact that = (FolderArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }
}
