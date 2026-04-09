package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;

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

    public static void setUpFolderNode(Node.Op folderNode, EntityFactory factory){
        folderNode.addChild(factory.createNode(new StringArtefact("types"))); // node fortypes
        folderNode.addChild(factory.createNode(new StringArtefact("folders"))); // other folders
    }


    public static void buildFolder(Workspace workspace, Folder parentFolder, Node folderNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException {
        if(folderNode.getArtifact().getData() instanceof FolderArtefact folderArtefact){
            Folder folder = Folder.CREATE(folderArtefact.getName(),workspace.its(parentFolder));


            Node typesNode = folderNode.getChildren().get(importantNodes.Types.ordinal());
            for (Node type : typesNode.getChildren()){
                InstanceTypeArtefact.build(workspace,folder,type,writerTypeManager);
            }
        }else {
            throw new NodeWrongArtefact("wrong node passed");
        }

    }

    public enum importantNodes {
        Types,
        SubFolders,
    }
}
