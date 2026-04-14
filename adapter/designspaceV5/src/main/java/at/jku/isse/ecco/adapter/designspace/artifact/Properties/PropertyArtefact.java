package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.foundation.Key;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.artifact.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.StringArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.util.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class PropertyArtefact implements ArtifactData {



    private final Long id;

    private final String name;
    private final Cardinality cardinality;


    public PropertyArtefact(Long id, String name, Cardinality cardinality) {
        this.id = id;
        this.name = name;
        this.cardinality = cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PropertyArtefact that = (PropertyArtefact) o;
        return Objects.equals(name, that.name) && cardinality == that.cardinality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cardinality);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public static void setupNode (PropertyArtefact artefact, Node.Op InstanceNode, EntityFactory entityFactory, Property property){
        switch (artefact.getCardinality()){
            case SINGLE:

                Node.Op single = entityFactory.createNode(artefact);
                InstanceNode.addChild(single);
                addValueNode(single, property.get(),entityFactory);

                break;
            case LIST:
                Node.Op listNode = entityFactory.createOrderedNode(artefact);
                InstanceNode.addChild(listNode);
                addListValueNodes(listNode,(ListProperty) property,entityFactory);
                break;

            case SET:
                Node.Op setNode = entityFactory.createNode(artefact);
                InstanceNode.addChild(setNode);
                addSetNodes(setNode,(SetProperty) property, entityFactory );
                break;

            case MAP:
                Node.Op mapNode = entityFactory.createNode(artefact);
                InstanceNode.addChild(mapNode);
                addMapNodes(mapNode,(MapProperty) property, entityFactory);
                break;
        }
    }




    private static void addListValueNodes(Node.Op propertyNode, ListProperty property,EntityFactory entityFactory){

        for (Object value: property.get()){
            addValueNode(propertyNode,value,entityFactory);
        }
    }

    private static void addSetNodes(Node.Op propertyNode, SetProperty property, EntityFactory entityFactory){
        for (Object value: property.get()){
           addValueNode(propertyNode,value,entityFactory);

        }
    }

    private static void addMapNodes(Node.Op propertyNode, MapProperty property, EntityFactory entityFactory){
        Map map = property.get();
        for (Object key: map.keySet()){
            Object value = map.get(key);

            Node.Op keyNode = entityFactory.createNode(new StringArtefact( ((Key) key).getKey()));
            addValueNode(keyNode,value,entityFactory);

            propertyNode.addChild(keyNode);
        }
    }

    private static void addValueNode(Node.Op propertyNode, Object value,EntityFactory entityFactory){

        if (value instanceof Instance instanceValue) {
            propertyNode.addChild(entityFactory.createNode(new ReferenceValueArtefact(instanceValue.getId(), instanceValue.getName())));
        }else{
            propertyNode.addChild(entityFactory.createNode(new SimpleValueArtifact<>(value)));
        }
    }

    // isntead of having cardinalities create seperate classes that handle the building on their own
    public static void build(Instance instance, Node propertyNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        if(propertyNode.getArtifact().getData() instanceof PropertyArtefact artefact){
            PropertyType propertyType = instance.getPropertyType(artefact.getName());
            if (propertyType == null) {
                // case does not have it
                //instance.getInstanceType().createPropertyType(artefact.getName(),artefact.getCardinality(),)
                throw new  ExecutionControl.NotImplementedException("creating Types is currently not implemented");
            }

            switch (artefact.getCardinality()){
                case SINGLE:
                    Node valueNode = propertyNode.getChildren().get(0);
                    instance.set(propertyType,getValueArtefact(valueNode).getValue());
                    break;
                case LIST:
                case SET:
                    buildSetList(instance,propertyNode,propertyType,writerTypeManager);
                    break;
                case MAP:
                    buildMap(instance,propertyNode,propertyType,writerTypeManager);
                    break;
            }


        }else {
            throw new NodeWrongArtefact("wrong node passed it isnt a instancetypeNode");
        }
    }

    public static void buildSetList(Instance instance, Node propertyNode, PropertyType propertyType, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        for (Node valueNode : propertyNode.getChildren()) {
            ValueArtefact valueArtefact = getValueArtefact(valueNode);
            instance.add(propertyType,valueArtefact.getValue());
        }
    }


    public static void buildMap(Instance instance, Node propertyNode, PropertyType propertyType, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        for (Node keyNode : propertyNode.getChildren()) {
            ValueArtefact keyArtefact = getValueArtefact(keyNode);
            ValueArtefact valueArtefact = getValueArtefact(keyNode.getChildren().get(0));
            if (keyArtefact.getValue() instanceof String keyString) {
                instance.add(propertyType,Key.of(keyString),valueArtefact.getValue());
            }else throw new NodeWrongArtefact("the keyNode should have a value of type Key");
        }
    }



    private static  ValueArtefact getValueArtefact(Node valueNode) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        if (valueNode.getArtifact().getData() instanceof ReferenceValueArtefact) {
            throw new  ExecutionControl.NotImplementedException("references");
        }else if (valueNode.getArtifact().getData() instanceof ValueArtefact valueartefact) {
            return valueartefact;
        } else {
            throw new NodeWrongArtefact(" wrong artefact for value");
        }
    }
}
