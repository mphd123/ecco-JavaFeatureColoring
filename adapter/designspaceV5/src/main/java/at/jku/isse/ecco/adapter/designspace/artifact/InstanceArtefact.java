package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.InstanceType;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.designspace.util.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.Node;

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


    public static void build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException {
        if(instanceNode.getArtifact().getData() instanceof InstanceArtefact artefact){
            InstanceType  instanceType = writerTypeManager.instanceTypeMap.get(artefact.getInstanceTypeId());
            if(instanceType == null) throw new TypeMangerException("the type for the Instance could not be found");
            Instance instance = Instance.CREATE(workspace, instanceType,artefact.getName(),folder);
            writerTypeManager.newToOriginalId.put(instance.getId(), artefact.getId());


            for (Node propertyTypeNode : instanceNode.getChildren()){
                    //PropertyArtefact.build(workspace,folder,propertyTypeNode,writerTypeManager);
            }
        }else {
            throw new NodeWrongArtefact("wrong node passed it isnt a instanceNode");
        }
    }


}
