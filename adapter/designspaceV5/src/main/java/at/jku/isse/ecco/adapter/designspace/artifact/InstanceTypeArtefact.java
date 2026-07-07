package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.Folder;

import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElementType;
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


public class InstanceTypeArtefact implements ArtifactData {

    public final String name;
    public final Long id;
    public final String languageWorkspaceName;
    Collection<Long> superIds = new HashSet<>();

    public InstanceTypeArtefact(String name, Long id, String languageWorkspaceName,Collection<WorkspaceElementType> superTypes) {
        this.name = name;
        this.id = id;
        this.languageWorkspaceName = languageWorkspaceName;
        superTypes.forEach(superType -> {
            superIds.add(superType.getId());
        });
    }


    public void build(Workspace workspace, Folder folder, Node typeNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
        WorkspaceElementType instanceType;
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
            instanceType = DesignSpace.getLanguageWorkspace(languageWorkspaceName).createElementType( name);
            writerTypeManager.newToOriginalId.put(instanceType.getId(), id);
            writerTypeManager.instanceTypeMap.put(name, instanceType);
        }
        for (Node instanceNode : typeNode.getChildren()) {
            InstanceArtefact instanceArtefact = (InstanceArtefact) instanceNode.getArtifact().getData();
            instanceArtefact.build(workspace, folder, instanceNode, writerTypeManager);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceTypeArtefact that = (InstanceTypeArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(superIds, that.superIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, superIds);
    }
}
