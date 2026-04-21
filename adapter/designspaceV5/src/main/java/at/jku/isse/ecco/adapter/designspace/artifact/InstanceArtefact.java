package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.InstanceType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.PropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public record InstanceArtefact(String name, Long id, Long instanceTypeId) implements ArtifactData {


    public void build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        InstanceType instanceType = writerTypeManager.instanceTypeMap.get(instanceTypeId);
        if (instanceType == null) throw new TypeMangerException("the type for the Instance could not be found");
        Instance instance = Instance.CREATE(workspace, instanceType, name, folder);
        writerTypeManager.newToOriginalId.put(instance.getId(), id);

        for (Node propertyTypeNode : instanceNode.getChildren()) {
            PropertyArtefact propertyArtefact = (PropertyArtefact) propertyTypeNode.getArtifact().getData();
            propertyArtefact.build(propertyTypeNode, instance, writerTypeManager);
        }
    }


}
