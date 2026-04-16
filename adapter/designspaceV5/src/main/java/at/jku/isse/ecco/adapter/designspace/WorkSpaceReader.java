package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.artifact.*;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.PropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;


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

    @Override
    public Set<Node.Op> read(DesignSpaceInfo base, DesignSpaceInfo[] input) {
        instanceTypeNodes = new HashMap<>();
        Workspace workspace = base.workspace();
        Folder commitFolder = workspace.its(base.folder());
        Node.Op pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace"));
        idMapper = base.idMapper();
        Node.Op checkinFolderNode = handleFolder(workspace,commitFolder,pluginNode);
        return Set.of(pluginNode);
    }

    private Node.Op handleFolder(Workspace workspace,Folder folder,Node.Op parentFolderNode){
        Node.Op folderNode = entityFactory.createOrderedNode(new FolderArtefact(folder.getName(),idMapper.getOriginalId(folder.getId()) ));
        FolderArtefact.setUpFolderNode(folderNode,entityFactory);
        try {
            Collection<Instance> instances = (Collection<Instance>) folder.get(Folder.INSTANCES);

            // instances contains other instances from other workspaces
            HashSet<InstanceType> addedInstanceTypes = new HashSet<>();
            if (instances != null) {
                for (Instance instance : instances){
                    // skip if the instance is not from another workspace
                    // check if this is okay as otherwise duplicated instances would be added to the instancetypes
                    if(! instance.getWorkspace().equals(workspace)) continue;

                    InstanceType instanceType = instance.getInstanceType();
                    if (instanceType == null) continue;
                    instanceType = workspace.its(instanceType);
                    if (instanceType == null) continue;

                    if(!addedInstanceTypes.contains(instanceType)){
                        addedInstanceTypes.add(instanceType);
                        handleInstanceType(workspace,folderNode,instanceType);
                    }

                    Instance workspaceInstance = workspace.its(instance);
                    if (workspaceInstance != null) {
                        handleInstance(workspace,workspaceInstance);
                    }
                }
                handleSubFolders(folder,folderNode,workspace);
                parentFolderNode.addChild(folderNode);
            }
        } catch (Exception e) {
            System.err.println("an error happened while reading from the folders error message  " +e);
        }


        return folderNode;
    }


    private void handleSubFolders(Folder folder,Node.Op folderNode, Workspace workspace) {
        Collection<Folder> children = folder.getSubFolders();
        if (children != null) {
            for (Folder childFolder : children) {
                Node.Op subFoldersNode  = folderNode.getChildren().get(FolderArtefact.importantNodes.SubFolders.ordinal());
                Node.Op childFolderNode = handleFolder(workspace,childFolder,subFoldersNode);
            }
        }
    }

    private void handleInstanceType(Workspace workspace, Node.Op parentNode,InstanceType instanceType){
        Node.Op typeNode = parentNode.getChildren().get(FolderArtefact.importantNodes.Types.ordinal());
        instanceType = workspace.its(instanceType);
        Node.Op instanceTypeNode = entityFactory.createNode(new InstanceTypeArtefact(instanceType.getName(),idMapper.getOriginalId(instanceType.getId()))); // check if should be ordered
        instanceTypeNodes.put(instanceType.getId(),instanceTypeNode);
        typeNode.addChild(instanceTypeNode);
    }

    private void handleInstance(Workspace workspace,Instance instance){
        instance = workspace.its(instance);
        InstanceType instanceType = instance.getInstanceType();
        instanceType = workspace.its(instanceType);
        if (!instanceTypeNodes.containsKey(instanceType.getId())) throw new RuntimeException("could not find InstancetypeNode");
        Node.Op instanceTypeNode = instanceTypeNodes.get(instanceType.getId());
        Node.Op instanceNode = entityFactory.createNode(new InstanceArtefact(instance.getName(), idMapper.getOriginalId(instance.getId()),instanceType.getId()));
        instanceTypeNode.addChild(instanceNode );

        Collection<PropertyType> propertyTypes = instanceType.getPropertyTypes();
        // have to figure out how to recreate it
        handleProperties(instance,instanceNode,propertyTypes);
    }

    private void handleProperties(Instance instance, Node.Op instanceNode, Collection<PropertyType> propertyTypes){

        for (PropertyType pt : propertyTypes) {
            if (pt instanceof  InitPropertyType) continue;
            // currently not sure how to handle

            Property property = instance.getProperty(pt);

            if (property.getName() != null &&
                    !property.getName().contains("@") &&
                    !property.getName().equals("modifiedBy") &&
                    !property.getName().equals("name")) {
                PropertyArtefact.setupNode(new PropertyArtefact(property.getId(), property.getName(), pt.getCardinality()),instanceNode,entityFactory,property);
            }
        }
    }

    @Override
    public Set<Node.Op> read(DesignSpaceInfo[] input) {
        return read(input[0],input);
    }

    @Override
    public void addListener(ReadListener listener) {
    }

    @Override
    public void removeListener(ReadListener listener) {

    }
}
