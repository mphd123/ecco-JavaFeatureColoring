package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.artifact.*;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.*;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;
import jdk.jshell.spi.ExecutionControl;


import java.nio.file.Path;
import java.util.*;



public class WorkSpaceReader implements ArtifactReader<DesignSpaceInfo, Set<Node.Op>> {
    private final EntityFactory entityFactory;
    private final List<ReadListener> listeners = new ArrayList<>();
    private HashMap<Long, Node.Op> instanceTypeNodes;
    IdMapper idMapper;
    // should be changed to local ones for the subfolders

    @Inject
    public WorkSpaceReader(EntityFactory entityFactory) {
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

    @Override
    public Set<Node.Op> read(DesignSpaceInfo base, DesignSpaceInfo[] input) {
        instanceTypeNodes = new HashMap<>();
        workspace = base.workspace();
        Folder commitFolder = workspace.its(base.folder());
        Node.Op pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace"));
        idMapper = base.idMapper();
        Node.Op checkinFolderNode = handleFolder(commitFolder,pluginNode);

        listeners.forEach(listener -> listener.fileReadEvent(Path.of(commitFolder.getPath()),this));
        return Set.of(pluginNode);
    }

    private Node.Op handleFolder(Folder folder,Node.Op parentFolderNode){
        Node.Op folderNode = entityFactory.createOrderedNode(new FolderArtefact(folder.getName(),idMapper.getOriginalId(folder.getId()) ));
        try {
             // Collection<Instance> instances = (Collection<Instance>) folder.get(Folder.INSTANCES);
            Collection<Instance> instances = folder.getInstances(workspace);

            // instances contains other instances from other workspaces
            HashSet<InstanceType> addedInstanceTypes = new HashSet<>();
            if (instances != null) {
                for (Instance instance : instances){
                    // skip if the instance is not from another workspace
                    // check if this is okay as otherwise duplicated instances would be added to the instanceTypes
                    if(! instance.getWorkspace().equals(workspace)) continue;

                    InstanceType instanceType = instance.getInstanceType();
                    if (instanceType == null) continue;
                    instanceType = workspace.its(instanceType);
                    if (instanceType == null) continue;

                    if(!addedInstanceTypes.contains(instanceType)){
                        addedInstanceTypes.add(instanceType);
                        handleInstanceType(folderNode,instanceType);
                    }
                    Instance workspaceInstance = workspace.its(instance);
                    if (workspaceInstance != null) {
                        handleInstance(workspaceInstance);
                    }
                }
                handleSubFolders(folder,folderNode);
                parentFolderNode.addChild(folderNode);
            }
        } catch (Exception e) {
            System.err.println("an error happened while reading from the folders error message  " +e);
            throw new RuntimeException(e);
        }
        return folderNode;
    }


    private void handleSubFolders(Folder folder,Node.Op folderNode) {
        Collection<Folder> children = folder.getSubFolders();
        if (children != null) {
            for (Folder childFolder : children) {
                Node.Op childFolderNode = handleFolder(childFolder,folderNode);
            }
        }
    }

    private void handleInstanceType(Node.Op folderNode,InstanceType instanceType){
        instanceType = workspace.its(instanceType);
        Node.Op instanceTypeNode = entityFactory.createNode(new InstanceTypeArtefact(instanceType.getName(),idMapper.getOriginalId(instanceType.getId())));
        instanceTypeNodes.put(instanceType.getId(),instanceTypeNode);
        folderNode.addChild(instanceTypeNode);
    }

    private void handleInstance(Instance instance) throws ExecutionControl.NotImplementedException {
        instance = workspace.its(instance);
        InstanceType instanceType = instance.getInstanceType();
        instanceType = workspace.its(instanceType);
        if (!instanceTypeNodes.containsKey(instanceType.getId())) throw new RuntimeException("could not find InstancetypeNode");
        Node.Op instanceTypeNode = instanceTypeNodes.get(instanceType.getId());
        Node.Op instanceNode = entityFactory.createNode(new InstanceArtefact(instance.getName(), idMapper.getOriginalId(instance.getId()),instanceType.getId()));
        instanceTypeNode.addChild(instanceNode );
        Collection<PropertyType> propertyTypes = instanceType.getPropertyTypes();
        handleProperties(instance,instanceNode,propertyTypes);
    }

    private void handleProperties(Instance instance, Node.Op instanceNode, Collection<PropertyType> propertyTypes) throws ExecutionControl.NotImplementedException {

        for (PropertyType pt : propertyTypes) {
            if (pt instanceof  InitPropertyType) continue;
            // currently not sure how to handle
            Property property = instance.getProperty(pt);
            if (property.getName() != null &&
                    !property.getName().contains("@") &&
                    !property.getName().equals("modifiedBy") &&
                    !property.getName().equals("name")) {
                PropertyArtefactInterface artefact =  createPropArtefact(property.getId(), property.getName(), pt.getCardinality());
                artefact.createNode(instanceNode,entityFactory,property);
            }
        }
    }

    private PropertyArtefactInterface createPropArtefact(Long id, String name, Cardinality cardinality) throws ExecutionControl.NotImplementedException {
        return switch (cardinality) {
            case MAP -> new MapPropertyArtefact(id, name, cardinality);
            case SET, LIST,ORDERED_SET -> new ListSetPropertyArtefact(id, name, cardinality);
            case SINGLE -> new SinglePropertyArtefact(id, name, cardinality);
            default -> throw new ExecutionControl.NotImplementedException("Unsupported Cardinality");
        };
    }

    @Override
    public Set<Node.Op> read(DesignSpaceInfo[] input) {
        return read(input[0],input);
    }

    @Override
    public void addListener(ReadListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ReadListener listener) {
        listeners.remove(listener);
    }
}
