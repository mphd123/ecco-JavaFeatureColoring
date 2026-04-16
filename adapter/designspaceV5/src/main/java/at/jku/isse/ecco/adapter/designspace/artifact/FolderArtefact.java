package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.InstanceType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class FolderArtefact implements ArtifactData {
    private final String name;



    private final Long id;
    public final Collection<Integer> instantTypeIds;

    public FolderArtefact(String name,Long id) {
        this.name = name;
        instantTypeIds = new HashSet<>();
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FolderArtefact that = (FolderArtefact) o;
        return Objects.equals(name, that.name);

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


    public static void buildFolder(Workspace workspace, Folder parentFolder, Node folderNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
        if(folderNode.getArtifact().getData() instanceof FolderArtefact folderArtefact){
            Folder folder = Folder.CREATE(folderArtefact.getName(),workspace.its(parentFolder));
            writerTypeManager.newToOriginalId.put(folder.getId(),folderArtefact.getId());

            for (Node child : folderNode.getChildren()) {
                if (child.getArtifact().getData() instanceof FolderArtefact) {
                    FolderArtefact.buildFolder(workspace,folder,child,writerTypeManager);
                }else if ( child.getArtifact().getData() instanceof InstanceTypeArtefact) {
                    InstanceTypeArtefact.build(workspace,folder,child,writerTypeManager);
                }
            }

        }else {
            throw new NodeWrongArtefact("wrong node passed");
        }

    }
}
