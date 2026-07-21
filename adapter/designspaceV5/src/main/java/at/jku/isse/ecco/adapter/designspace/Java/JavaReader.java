package at.jku.isse.ecco.adapter.designspace.Java;

import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.commons.Key;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.designspace.domains.Java8;
import at.jku.isse.ecco.EccoException;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.DesignSpacePlugin;
import at.jku.isse.ecco.adapter.designspace.Java.artefacts.JavaElement;
import at.jku.isse.ecco.adapter.designspace.Java.artefacts.TypeArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.StringArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;

import javax.lang.model.util.Elements;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static at.jku.isse.ecco.adapter.designspace.DesignSpaceModule.javaAdpaterString;

public class JavaReader implements ArtifactReader<DesignSpaceInfo, Set<Node.Op>> {
    private final EntityFactory entityFactory;
    private final List<ReadListener> listeners = new ArrayList<>();
    private HashMap<Long, Node.Op> instanceTypeNodes;

    private final Set<WorkspaceElement> processedElements = new HashSet<>();
    private Java8 java;
    IdMapper idMapper;
    // should be changed to local ones for the subfolders

    @Inject
    public JavaReader(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public String getPluginId() {
        return new DesignSpacePlugin().getPluginId();
    }

    @Override
    public Map<Integer, String[]> getPrioritizedPatterns() {
        return Map.of();
    }

    private Workspace workspace;

    private EccoException exception = null;
    private List<Exception> errors = new ArrayList<>();


    // to be correct here should only be one per Name if a name is shared there is a conflict



    @Override
    public Set<Node.Op> read(DesignSpaceInfo base, DesignSpaceInfo[] input) {
        instanceTypeNodes = new HashMap<>();
        workspace = base.workspace();
        Folder commitFolder = base.folder();
        Logger.debug = base.printDebug();
        idMapper = base.idMapper();

        Node.Op pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace Java"));
        handleProject(commitFolder, pluginNode);
        if (!errors.isEmpty()){
            System.err.println("While reading errors the following erros happend");
            errors.forEach(e -> e.printStackTrace());
        }

        return Set.of(pluginNode);
    }

    @Override
    public Set<Node.Op> read(DesignSpaceInfo[] input)  {
        return read(input[0],input);
    }

    @Override
    public void addListener(ReadListener listener) {

    }

    @Override
    public void removeListener(ReadListener listener) {

    }


    private void handleProject(Folder folder,Node.Op pluginNode){
        if(Java8.JAVA_PROJECT == null) {
            throw new EccoException("java 8 not initialized");
        }


        Set<WorkspaceElement> projects = folder.getWorkspaceElementContents(workspace).stream().filter(workspaceElement -> workspaceElement.isInstanceOf(Java8.JAVA_PROJECT)).collect(Collectors.toSet());
        for (WorkspaceElement project : projects){
            Node.Op projectNode = handleJavaElement(pluginNode,project);
            pluginNode.addChild(projectNode);
        }
    }


    private Node.Op handleJavaElement(Node.Op parentNode, WorkspaceElement element){
        if(processedElements.contains(element)){ return null; } // already handled prevents circles
        System.out.println("handeling" + element);
        Node.Op ElementNode = entityFactory.createOrderedNode(new JavaElement(element.getName(),element.getInstanceOf().getQualifiedName()));
        if (element == null) {
            System.err.println(" during the Read process for java elements one child element is null");
            return null;

        }
        if (!element.isInstanceOf(Java8.JAVA_ELEMENT)){
            //System.err.println(" during the Read process for java elements one child element does not belong to java8" + element +" instance of " + element.getInstanceOf());
            return null;
        }
        for (WorkspacePropertyType propType : element.getInstanceOf().getAllPropertyTypes()) {
            if (propType.isContained()) continue; // handled by other
            Node.Op propTypeNode = entityFactory.createOrderedNode(new TypeArtefact(propType.getQualifiedName(),propType.getCardinality()));

            Node childElement;
            if (propType.getCardinality().equals(Cardinality.SINGLE)){
                addValueNode(propTypeNode,element.getOrCreateProperty(propType).get());
            }else if (propType.getCardinality().equals(Cardinality.MAP)) {

                element.getMap(propType).forEach((key, value) -> {
                    Node.Op keyNode = entityFactory.createNode(new StringArtefact( ((Key) key).getName()));
                    addValueNode(keyNode,value);
                    propTypeNode.addChild(keyNode);
                });

            } else {
                Collection<Object> values = element.getCollection(propType);
                for (Object value : values) {
                    addValueNode(propTypeNode,value);
                }


            }
            ElementNode.addChild(propTypeNode);
            processedElements.add(element);
        }
        return ElementNode;
    }



    void addValueNode(Node.Op propertyNode, Object value){
        if (value == null)return;
        if (value instanceof WorkspaceElement instanceValue) {
            Long originalId = idMapper.getOriginalId(instanceValue.getId());
            Node.Op element = handleJavaElement(propertyNode, instanceValue);
            if (element != null){
                propertyNode.addChild(element);
            }
            if (originalId == null)  Logger.log("Debug Reader AddValueNode : Value= Warning original id was null for " + instanceValue.getId());
            else  Logger.log("Debug Reader AddValueNode : Value= " +originalId);
        }else{
            propertyNode.addChild(entityFactory.createNode(new SimpleValueArtifact<>(value)));
            if (value == null)  Logger.log("Debug Reader AddValueNode :  Value= " +null);
            else  Logger.log("Debug Reader AddValueNode :  Value= " +value.toString());

        }

    }


    @Override
    public String toString() {
        return javaAdpaterString;
    }
}
