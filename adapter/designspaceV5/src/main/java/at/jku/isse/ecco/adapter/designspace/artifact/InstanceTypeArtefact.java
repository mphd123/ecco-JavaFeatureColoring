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

import java.util.Objects;

public class InstanceTypeArtefact implements ArtifactData {
    private final String name;
    private final Long id;

    public InstanceTypeArtefact(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceTypeArtefact that = (InstanceTypeArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public static void build(Workspace workspace, Folder folder, Node typeNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException, InstanceTypeException {
        if(typeNode.getArtifact().getData() instanceof InstanceTypeArtefact artefact){
            InstanceType instanceType;
            if(writerTypeManager.instanceTypeMap.containsKey(artefact.getId())){
                if(writerTypeManager.instanceTypeMap.get(artefact.getId()).getName().equals(artefact.getName())){
                    instanceType =writerTypeManager.instanceTypeMap.get(artefact.getId());
                    if (!instanceType.getName().equals(artefact.name)) throw new InstanceTypeException(String.format("the names of the artefact and the already existing Type are not equal artefact[%s] . existing[%s]",artefact.getName(),instanceType.getName()));

                }else {
                    throw new TypeMangerException("instanceTypeMap has something with the same id but a different name");
                }
            }else {
                // need to support handle supertypes
                // todo and handle the id reassignment at the end
                instanceType = InstanceType.CREATE(workspace,artefact.getName());
                writerTypeManager.newToOriginalId.put(instanceType.getId(), artefact.getId());
                writerTypeManager.instanceTypeMap.put(artefact.id, instanceType);
            }






            for (Node instance : typeNode.getChildren()){
                InstanceArtefact.build(workspace, folder, instance, writerTypeManager);
            }
        }else {
            throw new NodeWrongArtefact("wrong node passed it isnt a instancetypeNode");
        }
    }

}
