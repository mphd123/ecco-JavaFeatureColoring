package at.jku.isse.ecco.adapter.designspace;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.EccoException;
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
import java.util.concurrent.atomic.AtomicReference;


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

    private EccoException  exception = null;


    // to be correct here should only be one per Name if a name is shared there is a conflict



    @Override
    public Set<Node.Op> read(DesignSpaceInfo base, DesignSpaceInfo[] input) {
        instanceTypeNodes = new HashMap<>();
        workspace = base.workspace();
        Folder commitFolder = base.folder();
        Node.Op pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace"));
        idMapper = base.idMapper();
        Node.Op checkinFolderNode = handleFolder(commitFolder,pluginNode,true);

        listeners.forEach(listener -> listener.fileReadEvent(Path.of(commitFolder.getQualifiedName()),this));


        return Set.of(pluginNode);
    }

    private Node.Op handleFolder(Folder folder,Node.Op parentFolderNode,boolean isCommitFolder){
        Node.Op folderNode = (isCommitFolder) ?  entityFactory.createOrderedNode(new CommitFolderArtefact()) : entityFactory.createOrderedNode(new FolderArtefact(folder.getName(),idMapper.getOriginalId(folder.getId()) ));
        try {

            Collection<WorkspaceElement> instances = folder.getWorkspaceElementContents(workspace);

            // instances contains other instances from other workspaces
            HashSet<WorkspaceElementType> addedInstanceTypes = new HashSet<>();
            if (instances != null) {
                for (WorkspaceElement instance : instances){
                    // skip if the instance is not from another workspace
                    // check if this is okay as otherwise duplicated instances would be added to the instanceTypes
                    if(! instance.getWorkspace().equals(workspace)) continue;

                    WorkspaceElementType instanceType = instance.getInstanceOf();
                    // ReferenceElementType l = instance.getReferenceElement(); // switch to reference later
                    if (instanceType == null) continue;
                    //instanceType = workspace.its(instanceType).getInstanceOf();
                    if (instanceType == null) continue;

                    if(!addedInstanceTypes.contains(instanceType)){
                        addedInstanceTypes.add(instanceType);
                        handleInstanceType(folderNode,instanceType);
                    }
                    WorkspaceElement workspaceInstance = workspace.its(instance);
                    if (workspaceInstance != null) {
                        handleInstance(workspaceInstance);
                    } else handleInstance(instance); // for testing fallback sicne its doesst seem to work in new version

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
                Node.Op childFolderNode = handleFolder(childFolder,folderNode,false);
            }
        }
    }

    private void handleInstanceType(Node.Op folderNode,WorkspaceElementType instanceType){
        AtomicReference<Collection<WorkspaceElementType>> superId = new AtomicReference<>();
        Optional.of(instanceType.getAllSubTypes()).ifPresentOrElse(superId::set,() -> superId.set(null));
        // here check if it supplies the languageWorkspaceName
        String languageWorkSpaceName = instanceType.getWorkspace().getName();
        Node.Op instanceTypeNode = entityFactory.createNode(new InstanceTypeArtefact(instanceType.getName(),idMapper.getOriginalId(instanceType.getId()), languageWorkSpaceName,superId.get()));
        instanceTypeNodes.put(instanceType.getId(),instanceTypeNode);
        try{
            folderNode.addChild(instanceTypeNode);
        }catch(EccoException e){
            if (e.getMessage().equals("An equivalent child is already contained. If multiple equivalent children are allowed use an ordered node.")){
                // in this case there is a duplicate name catch this exception because i want to collect all duplicate names for convenience
                exception = e;
            }else
                throw e;
        }

    }

    private void handleInstance(WorkspaceElement instance) throws ExecutionControl.NotImplementedException {
        WorkspaceElementType instanceType = instance.getInstanceOf();
        if (!instanceTypeNodes.containsKey(instanceType.getId())) throw new RuntimeException("could not find InstancetypeNode");
        Node.Op instanceTypeNode = instanceTypeNodes.get(instanceType.getId());
        Node.Op instanceNode = entityFactory.createNode(new InstanceArtefact(instance.getName(), idMapper.getOriginalId(instance.getId()),instanceType.getId()));


        try{
            instanceTypeNode.addChild(instanceNode );
        }catch(EccoException e){
            if (e.getMessage().equals("An equivalent child is already contained. If multiple equivalent children are allowed use an ordered node.")){
                // in this case there is a duplicate name catch this exception because i want to collect all duplicate names for convenience
                exception = e;
            }else
                throw e;
        }
        Collection<WorkspacePropertyType> propertyTypes = instanceType.getAllPropertyTypes();
        handleProperties(instance, instanceType,instanceNode,propertyTypes);
    }

    private void handleProperties(WorkspaceElement instance,WorkspaceElementType instanceType, Node.Op instanceNode, Collection<WorkspacePropertyType> propertyTypes) throws ExecutionControl.NotImplementedException {

        for (WorkspacePropertyType pt : propertyTypes) {
            //if (pt instanceof  InitPropertyType) continue;/ currently not sure how to handle

            WorkspaceProperty<Object> property = instance.getOrCreateProperty(pt);
            if (property.getName() != null &&
                    !property.getName().contains("@") &&
                    !property.getName().equals("modifiedBy") &&
                    !property.getName().equals("name")) {
                PropertyArtefactInterface artefact =  createPropArtefact(property.getId(),instanceType, property.getName(), pt.getCardinality());
                artefact.createNode(instanceNode,entityFactory,property, idMapper);
            }
        }
    }

    private PropertyArtefactInterface createPropArtefact(Long id, WorkspaceElementType instanceType, String propName, Cardinality cardinality) throws ExecutionControl.NotImplementedException {
        String qualifiedPropertyName =  propName;// instanceType.getQualifiedName() + "::" + propName; // changed to be finable
        return switch (cardinality) {
            case MAP -> new MapPropertyArtefact(id, qualifiedPropertyName, cardinality);
            case UNORDERED_SET, LIST, ORDERED_SET -> new ListSetPropertyArtefact(id, qualifiedPropertyName, cardinality);
            case SINGLE -> new SinglePropertyArtefact(id, qualifiedPropertyName, cardinality);
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
