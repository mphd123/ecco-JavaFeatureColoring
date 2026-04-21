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

public class InstanceArtefact implements ArtifactData {


    private final String name;
    private final Long id;
    private final Long instanceTypeId;


    public InstanceArtefact(String name, Long id, Long instanceTypeId) {
        this.name = name;
        this.id = id;
        this.instanceTypeId = instanceTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceArtefact that = (InstanceArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    public Long getInstanceTypeId() {
        return instanceTypeId;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }


    public void build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
            InstanceType  instanceType = writerTypeManager.instanceTypeMap.get(instanceTypeId);
            if(instanceType == null) throw new TypeMangerException("the type for the Instance could not be found");
            Instance instance = Instance.CREATE(workspace, instanceType,name,folder);
            writerTypeManager.newToOriginalId.put(instance.getId(), id);

            for (Node propertyTypeNode : instanceNode.getChildren()){
                PropertyArtefact propertyArtefact = (PropertyArtefact) propertyTypeNode.getArtifact().getData();
                propertyArtefact.build(propertyTypeNode, instance,writerTypeManager);
            }
    }


}
