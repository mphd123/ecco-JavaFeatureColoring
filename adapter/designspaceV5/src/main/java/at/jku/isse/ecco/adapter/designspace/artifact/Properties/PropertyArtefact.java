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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class PropertyArtefact implements ArtifactData {

    private final String name;
    private final Cardinality cardinality;


    public PropertyArtefact(String name, Cardinality cardinality) {
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

    public Cardinality getCardinality() {
        return cardinality;
    }

    public static void setupNode (PropertyArtefact artefact, Node.Op InstanceNode, EntityFactory entityFactory, Property property){
        switch (artefact.getCardinality()){
            case SINGLE:

                Node.Op single = entityFactory.createNode(artefact);
                InstanceNode.addChild(single);
                addValueNode(single, property,entityFactory);

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
        HashSet<Object> set = new HashSet<>();
        for (Object value: property.get()){
            if (set.add(value))addValueNode(propertyNode,value,entityFactory);

        }
    }

    private static void addMapNodes(Node.Op propertyNode, MapProperty property, EntityFactory entityFactory){
        HashMap<Key, Object> map = new HashMap<>();
        for (Object key: map.keySet()){
            Object value = map.get(key);
            String keyString = key.toString();

            Node.Op keyNode = entityFactory.createNode(new StringArtefact(keyString));
            addValueNode(keyNode,value,entityFactory);
        }
    }

    private static void addValueNode(Node.Op propertyNode, Object value,EntityFactory entityFactory){
        if (value instanceof Instance instanceValue) {
            propertyNode.addChild(entityFactory.createNode(new ReferenceValueArtefact(instanceValue.getId(), instanceValue.getName())));
        }else{
            propertyNode.addChild(entityFactory.createNode(new SimpleValueArtifact<>(value)));
        }
    }


    public static void build(Workspace workspace, Instance instance, Node propertyTypeNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException {
        if(propertyTypeNode.getArtifact() instanceof PropertyArtefact artefact){
            PropertyType propertyType = instance.getPropertyType(artefact.getName());
            if (propertyType == null) {
                // case does not have it
                //instance.getInstanceType().createPropertyType(artefact.getName(),artefact.getCardinality(),)
            }





            for (Node property : propertyTypeNode.getChildren()){

            }
        }else {
            throw new NodeWrongArtefact("wrong node passed it isnt a instancetypeNode");
        }
    }

    public static void buildSingle(Workspace workspace, Instance instance, Node propertyTypeNode, PropertyType propertyType,WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException {
        Node valueNode = propertyTypeNode.getChildren().get(0);
        if (valueNode.getArtifact() instanceof  ReferenceValueArtefact ref){

        }
            //WriterTypeManager.
            //instance.set(propertyType,writerTypeManager.instanceTypeMap.get(ref.))
    }
}
