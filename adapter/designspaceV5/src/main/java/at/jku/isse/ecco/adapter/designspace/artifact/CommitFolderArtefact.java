package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.ecco.adapter.designspace.WorkSpaceWriter;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public class CommitFolderArtefact implements ArtifactData {


    public void buildFolder(Folder parentFolder, Node folderNode, WorkSpaceWriter writer) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
        for (Node child : folderNode.getChildren()) {
            if (child.getArtifact().getData() instanceof FolderArtefact subFolder) {
                subFolder.buildFolder(parentFolder, child, writer);
            } else if (child.getArtifact().getData() instanceof InstanceTypeArtefact instanceTypeArtefact) {
                instanceTypeArtefact.build(child, parentFolder, writer);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass());
    }
}
