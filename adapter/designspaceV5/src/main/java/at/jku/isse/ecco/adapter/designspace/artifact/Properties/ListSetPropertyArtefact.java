package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.foundation.CollectionProperty;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.artifact.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public class ListSetPropertyArtefact extends PropertyArtefact {

    public ListSetPropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }

    public void createNode(Node.Op InstanceNode, EntityFactory entityFactory, Property property) throws ExecutionControl.NotImplementedException {
        Node.Op setNode = entityFactory.createNode(this);
        InstanceNode.addChild(setNode);
        addNodes(setNode,(CollectionProperty) property, entityFactory );
    }

    private void addNodes(Node.Op propertyNode, CollectionProperty property, EntityFactory entityFactory) throws ExecutionControl.NotImplementedException {
        // not sure why CollectionProperty does not have a get
        if (property instanceof SetProperty setProperty) {
            for (Object value: setProperty.get()){
                addValueNode(propertyNode,value,entityFactory);
            }
        }else if (property instanceof ListProperty listProperty) {
            for (Object value: listProperty.get()){
                addValueNode(propertyNode,value,entityFactory);
            }
        }else throw new ExecutionControl.NotImplementedException("for handling collectionProperties only List and Sets are supported");
    }

    public void build(Node propertyNode, Instance instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        PropertyType propertyType = instance.getPropertyType(name);
        for (Node valueNode : propertyNode.getChildren()) {
            ValueArtefact<?> valueArtefact = getValueArtefact(valueNode);
            instance.add(propertyType,valueArtefact.getValue());
        }
    }
}
