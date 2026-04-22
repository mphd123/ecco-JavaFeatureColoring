package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.foundation.CollectionProperty;
import at.jku.isse.designspace.core.model.*;
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

import java.util.*;

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
        // for sets there should be no duplicates in hte nodes
        List<ValueArtefact<?>> list = new ArrayList<>();
        for (Node valueNode : propertyNode.getChildren()) {
            ValueArtefact<?> valueArtefact = getValueArtefact(valueNode);
            list.add(valueArtefact);
        }
        setCollectionPropValue(instance,propertyType,list,writerTypeManager);
    }


    private void setCollectionPropValue(Instance instance, PropertyType propertyType, Collection<ValueArtefact<?>> artefactCollection, WriterTypeManager writerTypeManager) {
        if(artefactCollection.isEmpty()) return;

        ValueArtefact<?> example = artefactCollection.stream().findAny().orElse(null); // they should all be the same artefact
        if (example instanceof  ReferenceValueArtefact referenceValueArtefact) {
            Collection<Long> collection;
            if(propertyType.getCardinality().equals(Cardinality.SET)) collection = new HashSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");

            artefactCollection.forEach(( value) ->  collection.add((Long) value.getValue()));
            writerTypeManager.refFixUps.add(new CollectionFixUp(instance,propertyType,collection));
        }else if (example instanceof SimpleValueArtifact<?> valueArtifact) {
            Collection<Object> collection;
            if(propertyType.getCardinality().equals(Cardinality.SET)) collection = new HashSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");
            artefactCollection.forEach(( value) ->  collection.add(value.getValue()));
            instance.setAll(propertyType, collection);
        } else throw  new RuntimeException("unexpected value");
    }


}
