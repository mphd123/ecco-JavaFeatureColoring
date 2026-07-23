package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.*;

import at.jku.isse.ecco.adapter.designspace.WorkSpaceWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.PropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public record InstanceArtefact(String name, Long id, Long instanceTypeId) implements ArtifactData {


    public void build(Node instanceNode,Folder folder, WorkSpaceWriter writer) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        WorkspaceElementType instanceType = writer.writerTypeManager.instanceTypeMap.get( instanceTypeId);
        if (instanceType == null) throw new TypeMangerException("the type for the Instance could not be found");
        WorkspaceElement instance = writer.workspace.createWorkspaceElement( instanceType, name, folder);
        writer.writerTypeManager.newToOriginalId.put(instance.getId(), id);
        Logger.log(" instance created old id was " + "id" + "new id is " + instance.getId(), instance);

        for (Node propertyTypeNode : instanceNode.getChildren()) {
            PropertyArtefact propertyArtefact = (PropertyArtefact) propertyTypeNode.getArtifact().getData();
            propertyArtefact.build(propertyTypeNode, instance,writer);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceArtefact that = (InstanceArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id) && Objects.equals( instanceTypeId, that. instanceTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id,  instanceTypeId);
    }
}
