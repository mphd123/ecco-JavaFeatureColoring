package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.commons.OrderedSet;
import at.jku.isse.designspace.core.model.*;
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

    public void build(Workspace workspace, Folder folder, Node propertyTypeNode, WorkspaceElement owningElement,WriterTypeManager writerTypeManager)  {

        WorkspacePropertyType property = DesignSpace.getPropertyType(qualifiedName);
        System.out.println("setting type" + property);

        if (cardinality.equals(Cardinality.SINGLE)){
            if (owningElement instanceof SimpleValueArtifact){}
            if (propertyTypeNode.getChildren().size() != 1){
                System.err.println("TypeArtefact: " + qualifiedName + "Single doent have a single ChildNode it has " + propertyTypeNode.getChildren().size() );
                if (propertyTypeNode.getChildren().isEmpty()) return;
            }
            handleSingleValue(owningElement,workspace,folder,propertyTypeNode.getChildren().get(0),writerTypeManager);
        } else if (cardinality.equals(Cardinality.MAP)){
            System.out.println("Java8 has no Map property");

        } else{
            WorkspacePropertyType propertyType = DesignSpace.getPropertyType(qualifiedName);
            setCollectionPropValue(owningElement,propertyType,propertyTypeNode.getChildren(),workspace,folder,writerTypeManager);

        }

    }

    public void handleSingleValue(WorkspaceElement owningElement,Workspace workspace, Folder folder, Node value ,WriterTypeManager writerTypeManager){
        WorkspacePropertyType propertyType = DesignSpace.getPropertyType(qualifiedName);
        if (value.getArtifact().getData() instanceof  JavaElement javaElement) {
            // contained elements get there prop set by the container  so skip them
            if (propertyType.isContained()) {
                return;
            }
            WorkspaceElement child = null;
            try {
                child = javaElement.build(workspace,folder,value,writerTypeManager);
            } catch (NodeWrongArtefact | TypeMangerException | ExecutionControl.NotImplementedException e) {
                throw new RuntimeException(e);
            }
            owningElement.set(propertyType,child);


        }else if (value.getArtifact().getData() instanceof SimpleValueArtifact<?> valueArtifact) {
            owningElement.set(propertyType,valueArtifact.getValue());
        } else throw  new RuntimeException("unexpected value");
    }




    private void setCollectionPropValue(WorkspaceElement instance, WorkspacePropertyType propertyType, List< ? extends Node> valueNodeCollection, Workspace workspace,Folder folder, WriterTypeManager writerTypeManager) {
        System.out.println("setting collection property " + propertyType);
        if( valueNodeCollection.isEmpty()) return;
        if (propertyType.isContained()) {
            return;
        }

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
                        collection.add(javaElement.build(workspace,folder,value,writerTypeManager));
                    } catch (NodeWrongArtefact | ExecutionControl.NotImplementedException | TypeMangerException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("CollectionProps " + qualifiedName +" order is for retrieved elements " +Arrays.toString(collection.toArray()) );
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
            System.out.println("CollectionProps " + qualifiedName +" order is for retrieved elements " +Arrays.toString(collection.toArray()) );
        } else throw  new RuntimeException("unexpected value");
    }
}
