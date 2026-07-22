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
import at.jku.isse.ecco.adapter.designspace.Java.artefacts.SingleJavaElement;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        processedElements.clear();
        SingleJavaElement.reset();


        Node.Op pluginNode;
        try{
            System.out.println("javareader commit");

            instanceTypeNodes = new HashMap<>();
            workspace = base.workspace();
            Folder commitFolder = base.folder();
            Logger.debug = base.printDebug();
            idMapper = base.idMapper();

            pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace Java"));
            handleProject(commitFolder, pluginNode);
            if (!errors.isEmpty()) {
                System.err.println("While reading errors the following erros happend");
                errors.forEach(e -> e.printStackTrace());
            }

            System.out.println("\n=== GENERATED TREE ===");
            printNodeTreeToConsole(pluginNode,10);
            printNodeTreeToFile(pluginNode,"CommitData.txt");
            System.out.println("==========================\n");
        }finally {
            processedElements.clear();
            SingleJavaElement.reset();
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
        if (element == null) {
            System.err.println(" during the Read process for java elements one child element is null");
            return null;

        }
        boolean isSingle = SingleJavaElement.allSingles.containsKey(element.getInstanceOf());


        if (processedElements.contains(element) && !isSingle) {
            return null;
        }


        System.out.println("handeling" + element);
        Node.Op ElementNode;
        if (SingleJavaElement.allSingles.containsKey(element.getInstanceOf())) {
            ElementNode = entityFactory.createOrderedNode(new SingleJavaElement(element.getName(),element.getInstanceOf().getQualifiedName()));
        }else{
            ElementNode = entityFactory.createOrderedNode(new JavaElement(element.getName(),element.getInstanceOf().getQualifiedName()));
        }


        if (!element.isInstanceOf(Java8.JAVA_ELEMENT)){
            //System.err.println(" during the Read process for java elements one child element does not belong to java8" + element +" instance of " + element.getInstanceOf());
            return null;
        }

        boolean firstTime = processedElements.add(element);


        if (isSingle && !firstTime) {
            return ElementNode;
        }
        for (WorkspacePropertyType propType : element.getInstanceOf().getAllPropertyTypes()) {

            if (!Filter.shouldProcessProperty(propType)) { // shouldProcessProperty(propType)
                continue;
            }

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



    public void printNodeTreeToFile(Node node, String filePath) {
        Path path = Paths.get(filePath);

        if (node == null) {
            deleteLogFile(path);
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("┌── " + formatNodeLabel(node));
            List<? extends Node> children = node.getChildren();

            for (int i = 0; i < children.size(); i++) {
                printNodeTreeRecursive(children.get(i), "", i == children.size() - 1, writer, 1, Integer.MAX_VALUE);
            }


            if (writer.checkError()) {
                throw new IOException("Stream error encountered while writing tree output.");
            }

            System.out.println("AST Tree saved successfully to: " + filePath);

        } catch (Exception e) {
            System.err.println("Failed to generate AST tree (" + e.getMessage() + "). Cleaning up old/incomplete log file...");
            deleteLogFile(path);
        }
    }

    private void deleteLogFile(Path path) {
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                System.out.println("Deleted invalid/previous log file: " + path.getFileName());
            }
        } catch (IOException e) {
            System.err.println("Could not delete log file: " + e.getMessage());
        }
    }

    public void printNodeTreeToConsole(Node node, int maxDepth) {
        if (node == null) return;
        PrintWriter writer = new PrintWriter(System.out);
        writer.println("┌── " + formatNodeLabel(node));
        List<? extends Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            printNodeTreeRecursive(children.get(i), "", i == children.size() - 1, writer, 1, maxDepth);
        }
        writer.flush(); // Ensure console output is flushed
    }

    private void printNodeTreeRecursive(Node node, String indent, boolean isLast, PrintWriter writer, int currentDepth, int maxDepth) {
        String marker = isLast ? "└── " : "├── ";

        writer.println(indent + marker + formatNodeLabel(node));

        String childIndent = indent + (isLast ? "    " : "│   ");
        List<? extends Node> children = node.getChildren();

        if (currentDepth >= maxDepth && !children.isEmpty()) {
            writer.println(childIndent + "└── ... [" + children.size() + " children hidden]");
            return;
        }

        for (int i = 0; i < children.size(); i++) {
            printNodeTreeRecursive(children.get(i), childIndent, i == children.size() - 1, writer, currentDepth + 1, maxDepth);
        }
    }

    private String formatNodeLabel(Node node) {
        Object artifact = node.getArtifact().getData();

        if (artifact instanceof JavaElement javaElement) {
            return "JavaElement [" + javaElement.typeName + "] name: '" + javaElement.name + "'";
        } else if (artifact instanceof SingleJavaElement singleElement) {
            return "SingleJavaElement [" + singleElement.typeName + "] name: '" + singleElement.name + "'";
        } else if (artifact instanceof TypeArtefact typeArtefact) {
            return "Property: " + typeArtefact.qualifiedName + " (" + typeArtefact.cardinality + ")";
        } else if (artifact instanceof StringArtefact stringArtefact) {
            return "StringArtefact: '" + stringArtefact.getValue() + "'";
        } else if (artifact instanceof SimpleValueArtifact<?> simpleValue) {
            return "Value: " + simpleValue.getValue();
        } else if (artifact != null) {
            return artifact.getClass().getSimpleName() + ": " + artifact.toString();
        }

        return "NullArtifact";
    }


    @Override
    public String toString() {
        return javaAdpaterString;
    }
}
