package at.jku.isse.ecco.adapter.designspace.artifact.Properties;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspaceProperty;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.RefProeprtyFixupRecord;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.SingleFixUp;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public class SinglePropertyArtefact extends PropertyArtefact {

    public SinglePropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }


    public void createNode(Node.Op InstanceNode, EntityFactory entityFactory, WorkspaceProperty<?> property, IdMapper idMapper){
        Node.Op single = entityFactory.createNode(this);
        InstanceNode.addChild(single);
        addValueNode(single, property.get(),entityFactory,idMapper);
    }

    public void build(Node propertyNode, WorkspaceElement instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        Node valueNode = propertyNode.getChildren().get(0);

        WorkspacePropertyType propertyType =  instance.getInstanceOf().getPropertyType(name);
        setSinglePropValue(instance,propertyType,getValueArtefact(valueNode),writerTypeManager);
    }

    private void setSinglePropValue(WorkspaceElement instance, WorkspacePropertyType propertyType, ValueArtefact<?> valueArtefact, WriterTypeManager writerTypeManager) {
        if (valueArtefact instanceof  ReferenceValueArtefact referenceValueArtefact) {
            writerTypeManager.refFixUps.add(new SingleFixUp(instance,propertyType,referenceValueArtefact.getValue()));
        }else if (valueArtefact instanceof SimpleValueArtifact<?> valueArtifact) {
            instance.set(propertyType,valueArtifact.getValue());
        } else throw  new RuntimeException("unexpected value");
    }
}
