package at.jku.isse.ecco.adapter.designspace;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.EccoException;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.artifact.*;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.ListSetPropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.MapPropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.PropertyArtefactInterface;
import at.jku.isse.ecco.adapter.designspace.artifact.Properties.SinglePropertyArtefact;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;
import jdk.jshell.spi.ExecutionControl;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static at.jku.isse.ecco.adapter.designspace.DesignSpaceModule.generalAdpaterString;
import static at.jku.isse.ecco.adapter.designspace.DesignSpaceModule.generalAdpaterV2String;


public class WorkSpaceReader implements ArtifactReader<DesignSpaceInfo, Set<Node.Op>> {
    public final EntityFactory entityFactory;
    private final List<ReadListener> listeners = new ArrayList<>();
    private HashMap<Long, Node.Op> instanceTypeNodes;


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

    public Workspace workspace;
    public Folder folder;
    public IdMapper idMapper;

    private EccoException exception = null;


    // to be correct here should only be one per Name if a name is shared there is a conflict


    @Override
    public Set<Node.Op> read(DesignSpaceInfo info, DesignSpaceInfo[] input) {
        info.checkIfInfoValid();

        instanceTypeNodes = new HashMap<>();
        workspace = info.workspace();
        folder = info.folder();
        Logger.debug = info.debugOptions().generalAdapterConsole();
        Node.Op pluginNode = entityFactory.createOrderedNode(new StringArtefact("plugin Node Designspace"));
        idMapper = info.idMapper();
        Node.Op checkinFolderNode = handleFolder(folder, pluginNode, true);

        listeners.forEach(listener -> listener.fileReadEvent(Path.of(folder.getQualifiedName()), this));

        if (!errors.isEmpty()) {
            System.err.println("While reading errors the following erros happend");
            errors.forEach(e -> e.printStackTrace());
        }

        return Set.of(pluginNode);
    }

    private Node.Op handleFolder(Folder folder, Node.Op parentFolderNode, boolean isCommitFolder) {


        Node.Op folderNode = (isCommitFolder) ? entityFactory.createOrderedNode(new CommitFolderArtefact()) : entityFactory.createOrderedNode(new FolderArtefact(folder.getName(), idMapper.getOriginalId(folder.getId())));
        try {

            Collection<WorkspaceElement> instances = folder.getWorkspaceElementContents(workspace);

            // instances contains other instances from other workspaces
            HashSet<WorkspaceElementType> addedInstanceTypes = new HashSet<>();
            if (instances != null) {
                for (WorkspaceElement instance : instances) {
                    // skip if the instance is not from another workspace
                    // check if this is okay as otherwise duplicated instances would be added to the instanceTypes
                    if (!instance.getWorkspace().equals(workspace)) continue;

                    WorkspaceElementType instanceType = instance.getInstanceOf();
                    // ReferenceElementType l = instance.getReferenceElement(); // switch to reference later
                    if (instanceType == null) continue;
                    //instanceType = workspace.its(instanceType).getInstanceOf();
                    if (instanceType == null) continue;

                    if (!addedInstanceTypes.contains(instanceType)) {
                        addedInstanceTypes.add(instanceType);
                        handleInstanceType(folderNode, instanceType);
                    }
                    WorkspaceElement workspaceInstance = workspace.its(instance);
                    if (workspaceInstance != null) {
                        handleInstance(workspaceInstance);
                    } else
                        handleInstance(instance); // for testing fallback sicne its doesst seem to work in new version

                }
                handleSubFolders(folder, folderNode);
                parentFolderNode.addChild(folderNode);
            }
        } catch (Exception e) {
            System.err.println("an error happened while reading from the folders error message  " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return folderNode;
    }


    private void handleSubFolders(Folder folder, Node.Op folderNode) {
        Collection<Folder> children = folder.getSubFolders();
        if (children != null) {
            for (Folder childFolder : children) {
                Node.Op childFolderNode = handleFolder(childFolder, folderNode, false);
            }
        }
    }

    private void handleInstanceType(Node.Op folderNode, WorkspaceElementType instanceType) {
        AtomicReference<Collection<WorkspaceElementType>> superId = new AtomicReference<>();
        Optional.of(instanceType.getAllSubTypes()).ifPresentOrElse(superId::set, () -> superId.set(null));
        // here check if it supplies the languageWorkspaceName
        String languageWorkSpaceName = instanceType.getWorkspace().getName();
        Node.Op instanceTypeNode = entityFactory.createNode(new InstanceTypeArtefact(instanceType.getName(), idMapper.getOriginalId(instanceType.getId()), languageWorkSpaceName, superId.get()));
        instanceTypeNodes.put(instanceType.getId(), instanceTypeNode);
        try {
            folderNode.addChild(instanceTypeNode);
        } catch (EccoException e) {
            if (e.getMessage().equals("An equivalent child is already contained. If multiple equivalent children are allowed use an ordered node.")) {
                // in this case there is a duplicate name catch this exception because i want to collect all duplicate names for convenience
                exception = e;
            } else
                throw e;
        }

    }

    private void handleInstance(WorkspaceElement instance) throws ExecutionControl.NotImplementedException {
        try {
            WorkspaceElementType instanceType = instance.getInstanceOf();
            if (!instanceTypeNodes.containsKey(instanceType.getId()))
                throw new RuntimeException("could not find InstancetypeNode");
            Node.Op instanceTypeNode = instanceTypeNodes.get(instanceType.getId());
            Node.Op instanceNode = entityFactory.createNode(new InstanceArtefact(instance.getName(), idMapper.getOriginalId(instance.getId()), instanceType.getId()));


            try {
                instanceTypeNode.addChild(instanceNode);
            } catch (EccoException e) {
                if (e.getMessage().equals("An equivalent child is already contained. If multiple equivalent children are allowed use an ordered node.")) {
                    // in this case there is a duplicate name catch this exception because i want to collect all duplicate names for convenience
                    exception = e;
                } else
                    throw e;
            }
            Collection<WorkspacePropertyType> propertyTypes = instanceType.getAllPropertyTypes();

            if (Logger.isToBeLoggedType(instance)) {
                Logger.enabledAndThenDisabled = true;
                Logger.log(" PropTypes for chosen instancetype", instance);
            }

            handleProperties(instance, instanceType, instanceNode, propertyTypes);

        } catch (Exception e) {
            errors.add(e);
            e.printStackTrace();

        } finally {
            if (Logger.isToBeLoggedType(instance)) Logger.enabledAndThenDisabled = false;
        }

    }

    private List<Exception> errors = new ArrayList<>();

    private void handleProperties(WorkspaceElement instance, WorkspaceElementType instanceType, Node.Op instanceNode, Collection<WorkspacePropertyType> propertyTypes) throws ExecutionControl.NotImplementedException {

        for (WorkspacePropertyType pt : propertyTypes) {

            try {
                Logger.log("Property= " + pt.getName());

                WorkspaceProperty<Object> property = instance.getOrCreateProperty(pt);
                // skip empty properties
                if (property.getRaw() == null) continue;

                // note also would like to properties that are opposed
                //if(pt.isContained()) continue;
                if (property.getName() != null &&
                        !property.getName().contains("@") &&
                        !property.getName().equals("modifiedBy") &&
                        !property.getName().equals("name")) {
                    PropertyArtefactInterface artefact = createPropArtefact(property.getId(), instanceType, property.getName(), pt.getCardinality());
                    artefact.createNode(instanceNode, property, this);
                }
            } catch (Exception e) {
                errors.add(e);
                e.printStackTrace();
            }


        }
    }

    private PropertyArtefactInterface createPropArtefact(Long id, WorkspaceElementType instanceType, String propName, Cardinality cardinality) throws ExecutionControl.NotImplementedException {

        String qualifiedPropertyName = propName;// instanceType.getQualifiedName() + "::" + propName; // changed to be findable
        return switch (cardinality) {
            case MAP -> new MapPropertyArtefact(id, qualifiedPropertyName, cardinality);
            case UNORDERED_SET, LIST, ORDERED_SET ->
                    new ListSetPropertyArtefact(id, qualifiedPropertyName, cardinality);
            case SINGLE -> new SinglePropertyArtefact(id, qualifiedPropertyName, cardinality);
            default -> throw new ExecutionControl.NotImplementedException("Unsupported Cardinality");
        };
    }

    @Override
    public Set<Node.Op> read(DesignSpaceInfo[] input) {
        return read(input[0], input);
    }

    @Override
    public void addListener(ReadListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ReadListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        return generalAdpaterString;
    }
}
