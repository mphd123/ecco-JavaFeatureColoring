package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.Property;
import at.jku.isse.designspace.core.model.PropertyType;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public class SinglePropertyArtefact extends PropertyArtefact {

    public SinglePropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }


    public void createNode(Node.Op InstanceNode, EntityFactory entityFactory, Property property){
        Node.Op single = entityFactory.createNode(this);
        InstanceNode.addChild(single);
        addValueNode(single, property.get(),entityFactory);
    }

    public void build(Node propertyNode, Instance instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        Node valueNode = propertyNode.getChildren().get(0);
        PropertyType propertyType = instance.getPropertyType(name);
        instance.set(propertyType,getValueArtefact(valueNode).getValue());
    }
}
