package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.artifact.*;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.PropertyArtefact;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;


import java.util.*;


public class WorkSpaceReaderNode implements ArtifactReader<Pair, Set<Node.Op>> {
    private final EntityFactory entityFactory;
    private final List<ReadListener> listeners = new ArrayList<>();
    private HashMap<Long, Node.Op> instanceTypeNodes;
    // should be changed to local ones for the subfolders

    @Inject
    public WorkSpaceReaderNode(EntityFactory entityFactory) {
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
    public Set<Node.Op> read(Pair base, Pair[] input) {
        instanceTypeNodes = new HashMap<>();
        Workspace workspace = base.workspace();
        Folder commitFolder = workspace.its(base.folder());

        Node.Op rootFolderNode = handleFolder(workspace,commitFolder,null);


        return Set.of(rootFolderNode);
    }

    private Node.Op handleFolder(Workspace workspace,Folder folder,Node.Op parentFolderNode){
        Node.Op folderNode = entityFactory.createOrderedNode(new FolderArtefact(folder.getName()));
        FolderArtefact.setUpFolderNode(folderNode,entityFactory);

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
                    handleInstanceType(workspace,folder,folderNode,instanceType);
                }


                Instance workspaceInstance = workspace.its(instance);
                if (workspaceInstance != null) {
                    handleInstance(workspace,folder,workspaceInstance);
                }
            }
        }

        Collection<Folder> children = folder.getSubFolders();

        if (children != null) {
            for (Folder childFolder : children) {
                Node.Op childFolderNode = handleFolder(workspace,childFolder,folderNode);
                if (parentFolderNode != null) {
                    Node.Op subFolders = parentFolderNode.getChildren().get(FolderArtefact.importantNodes.SubFolders.ordinal());
                    subFolders.addChild(childFolderNode);
                }
            }
        }

        return folderNode;
    }

    private void handleInstanceType(Workspace workspace, Folder parentFolder, Node.Op parentNode,InstanceType instanceType){
        Node.Op typeNode = parentNode.getChildren().get(FolderArtefact.importantNodes.Types.ordinal());

        instanceType = workspace.its(instanceType);

        Node.Op instanceTypeNode = entityFactory.createNode(new InstanceTypeArtefact(instanceType.getName(),instanceType.getId())); // check if should be ordered
        instanceTypeNodes.put(instanceType.getId(),instanceTypeNode);

        typeNode.addChild(instanceTypeNode);

    }

    private void handleInstance(Workspace workspace, Folder parentFolder,Instance instance){


        instance = workspace.its(instance);

        InstanceType instanceType = instance.getInstanceType();
        instanceType = workspace.its(instanceType);

        if (!instanceTypeNodes.containsKey(instanceType.getId())) throw new RuntimeException("could not find InstancetypeNode");
        Node.Op instanceTypeNode = instanceTypeNodes.get(instanceType.getId());

        Node.Op instanceNode = entityFactory.createNode(new InstanceArtefact(instance.getName(),instance.getId(),instanceType.getId()));
        instanceTypeNode.addChild(instanceNode );

        Collection<PropertyType> propertyTypes = instanceType.getPropertyTypes();
        // have to figure out how to recreate it
        //handleProperties(instance,instanceNode,propertyTypes);
    }

    private void handleProperties(Instance instance, Node.Op instanceNode, Collection<PropertyType> propertyTypes){

        for (PropertyType pt : propertyTypes) {
            Property property = instance.getProperty(pt);
            if (property.getName() != null &&
                    !property.getName().contains("@") &&
                    !property.getName().equals("modifiedBy") &&
                    !property.getName().equals("name")) {

                PropertyArtefact.setupNode(new PropertyArtefact(property.getName(), pt.getCardinality()),instanceNode,entityFactory,property);
            }
        }
    }

    @Override
    public Set<Node.Op> read(Pair[] input) {
        return read(input[0],input);
    }

    @Override
    public void addListener(ReadListener listener) {

    }

    @Override
    public void removeListener(ReadListener listener) {

    }
}
