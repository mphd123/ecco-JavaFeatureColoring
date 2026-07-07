package at.jku.isse.ecco.adapter.designspace.artifact.Properties;



import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.commons.OrderedSet;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.RefProeprtyFixupRecord;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.CollectionFixUp;

import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;
import org.apache.logging.log4j.CloseableThreadContext;

import java.util.*;

public class ListSetPropertyArtefact extends PropertyArtefact {

    public ListSetPropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }

    public void createNode(Node.Op InstanceNode, EntityFactory entityFactory, WorkspaceProperty<?> property, IdMapper idMapper) throws ExecutionControl.NotImplementedException {
        Node.Op setNode = entityFactory.createNode(this);
        InstanceNode.addChild(setNode);
        addNodes(setNode, property, entityFactory,idMapper );
    }

    private void addNodes(Node.Op propertyNode, WorkspaceProperty<?> property, EntityFactory entityFactory,IdMapper idMapper) throws ExecutionControl.NotImplementedException {
        // not sure why CollectionProperty does not have a get
        if (property instanceof WorkspacePropertyUnorderedSet<?> || property instanceof WorkspacePropertyList<?> || property instanceof WorkspacePropertyOrderedSet<?>) {
            for (Object value: property.getCollection()){
                addValueNode(propertyNode,value,entityFactory,idMapper);
            }
        } else throw new ExecutionControl.NotImplementedException("for handling collectionProperties only List and (ordered) Sets are supported");
    }

    public void build(Node propertyNode, WorkspaceElement instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        WorkspacePropertyType propertyType = instance.getInstanceOf().getPropertyType(name);
        // for sets there should be no duplicates in hte nodes
        List<ValueArtefact<?>> list = new ArrayList<>();
        for (Node valueNode : propertyNode.getChildren()) {
            ValueArtefact<?> valueArtefact = getValueArtefact(valueNode);
            list.add(valueArtefact);
        }
        setCollectionPropValue(instance,propertyType,list,writerTypeManager);
    }


    private void setCollectionPropValue( WorkspaceElement instance, WorkspacePropertyType propertyType, Collection<ValueArtefact<?>> artefactCollection, WriterTypeManager writerTypeManager) {
        if(artefactCollection.isEmpty()) return;

        ValueArtefact<?> example = artefactCollection.stream().findAny().orElse(null); // they should all be the same artefact
        if (example instanceof  ReferenceValueArtefact) {
            Collection<Long> collection;
            if(propertyType.getCardinality().equals(Cardinality.UNORDERED_SET) || propertyType.getCardinality().equals(Cardinality.ORDERED_SET )) collection = new OrderedSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");

            artefactCollection.forEach(( value) ->  collection.add((Long) value.getValue()));
            writerTypeManager.refFixUps.add(new CollectionFixUp(instance,propertyType,collection));
        }else if (example instanceof SimpleValueArtifact<?>) {
            Collection<Object> collection;
            if(propertyType.getCardinality().equals(Cardinality.UNORDERED_SET)|| propertyType.getCardinality().equals(Cardinality.ORDERED_SET )) collection = new OrderedSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");
            artefactCollection.forEach(( value) ->  collection.add(value.getValue()));
            instance.setAll(propertyType, collection);
        } else throw  new RuntimeException("unexpected value");
    }


}
