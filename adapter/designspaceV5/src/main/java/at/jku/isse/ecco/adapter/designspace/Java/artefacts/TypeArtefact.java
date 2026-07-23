package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.commons.OrderedSet;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.Java.JavaWriter;
import at.jku.isse.ecco.adapter.designspace.Java.TreeLogger;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.CollectionFixUp;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.*;

public class TypeArtefact implements JavaArtefact {
    public final String qualifiedName;
    public final Cardinality cardinality;

    public TypeArtefact(String qualifiedName, Cardinality cardinality) {
        this.qualifiedName = qualifiedName;
        this.cardinality = cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TypeArtefact that = (TypeArtefact) o;
        return Objects.equals(qualifiedName, that.qualifiedName) && cardinality == that.cardinality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, cardinality);
    }


    public void build(Node propertyTypeNode, WorkspaceElement owningElement, JavaWriter javaWriter)  {


        try (var scope = TreeLogger.enter("Property: " + qualifiedName + " (" + cardinality + ")")) {

            if (cardinality.equals(Cardinality.SINGLE)) {
                if (propertyTypeNode.getChildren().isEmpty()) {
                    TreeLogger.log("(empty single property)");
                    return;
                }
                handleSingleValue(owningElement, propertyTypeNode.getChildren().get(0), javaWriter);
            } else if (!cardinality.equals(Cardinality.MAP)) {
                WorkspacePropertyType propertyType = DesignSpace.getPropertyType(qualifiedName);
                setCollectionPropValue(owningElement, propertyType, propertyTypeNode.getChildren(), javaWriter);
            }
        }
    }

    public void handleSingleValue(WorkspaceElement owningElement, Node value ,JavaWriter javaWriter){
        WorkspacePropertyType propertyType = DesignSpace.getPropertyType(qualifiedName);
        if (value.getArtifact().getData() instanceof  JavaElement javaElement) {
            // contained elements get there prop set by the container  so skip them
            //if (propertyType.isContained()) {
            //    return;
            //}
            WorkspaceElement child = null;
            try {
                child = javaElement.build(value,javaWriter);
            } catch (NodeWrongArtefact | TypeMangerException | ExecutionControl.NotImplementedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("set  " + child);
            owningElement.set(propertyType,child);


        }else if (value.getArtifact().getData() instanceof SimpleValueArtifact<?> valueArtifact) {
            owningElement.set(propertyType,valueArtifact.getValue());
        } else throw  new RuntimeException("unexpected value");
    }




    private void setCollectionPropValue(WorkspaceElement instance, WorkspacePropertyType propertyType, List< ? extends Node> valueNodeCollection, JavaWriter javaWriter) {

        if( valueNodeCollection.isEmpty()) {

            return;}


        Node example =  valueNodeCollection.stream().findAny().orElse(null); // they should all be the same artefact
        if (example == null) {
            System.err.println("null collection in TypeArtefact");
            return;
        }
        if (example.getArtifact().getData() instanceof  JavaElement) {
            Collection<WorkspaceElement> collection;
            if(propertyType.getCardinality().equals(Cardinality.UNORDERED_SET) || propertyType.getCardinality().equals(Cardinality.ORDERED_SET )) collection = new OrderedSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");


            valueNodeCollection.forEach(( value) ->  {
                if (value.getArtifact().getData() instanceof JavaElement javaElement){
                    try {
                        collection.add(javaElement.build(value,javaWriter));
                    } catch (NodeWrongArtefact | ExecutionControl.NotImplementedException | TypeMangerException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            //System.out.println("CollectionProps " + qualifiedName +" for " + instance+" order is for retrieved elements " +Arrays.toString(collection.toArray()) );
            instance.setAll(propertyType, collection);


        }else if (example.getArtifact().getData() instanceof SimpleValueArtifact<?>) {
            Collection<Object> collection;
            if(propertyType.getCardinality().equals(Cardinality.UNORDERED_SET)|| propertyType.getCardinality().equals(Cardinality.ORDERED_SET )) collection = new OrderedSet<>();
            else if (propertyType.getCardinality().equals(Cardinality.LIST)) collection = new ArrayList<>();
            else throw new RuntimeException("ListSetArtefact received invalid Cardinality");
            valueNodeCollection.forEach(( value) ->  {
                if (value.getArtifact().getData() instanceof SimpleValueArtifact<?> simpleValueArtifact){
                    collection.add(simpleValueArtifact.getValue());
                }
            });
            instance.setAll(propertyType, collection);
            //System.out.println("CollectionProps " + qualifiedName +" for " + instance+" order is for retrieved elements " +Arrays.toString(collection.toArray()) );
        } else throw  new RuntimeException("unexpected value");
    }
}
