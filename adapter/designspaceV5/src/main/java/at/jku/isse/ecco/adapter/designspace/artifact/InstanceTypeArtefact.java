package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.InstanceType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public record InstanceTypeArtefact(String name, Long id) implements ArtifactData {

    public void build(Workspace workspace, Folder folder, Node typeNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
        InstanceType instanceType;
        if (writerTypeManager.instanceTypeMap.containsKey(id)) {
            if (writerTypeManager.instanceTypeMap.get(id).getName().equals(name)) {
                instanceType = writerTypeManager.instanceTypeMap.get(id);
                if (!instanceType.getName().equals(name))
                    throw new InstanceTypeException(String.format("the names of the artefact and the already existing Type are not equal artefact[%s] . existing[%s]", name, instanceType.getName()));
            } else {
                throw new TypeMangerException("instanceTypeMap has something with the same id but a different name");
            }
        } else {
            // need to support handle supertypes
            // todo and handle the id reassignment at the end
            instanceType = InstanceType.CREATE(workspace, name);
            writerTypeManager.newToOriginalId.put(instanceType.getId(), id);
            writerTypeManager.instanceTypeMap.put(id, instanceType);
        }
        for (Node instanceNode : typeNode.getChildren()) {
            InstanceArtefact instanceArtefact = (InstanceArtefact) instanceNode.getArtifact().getData();
            instanceArtefact.build(workspace, folder, instanceNode, writerTypeManager);
        }
    }
}
