package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.domains.Java8;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingleJavaElement extends JavaElement {

    public SingleJavaElement(String name, String typeName) {
        super(name, typeName);
    }

    // this should be fine
    public static final Map<WorkspaceElementType, WorkspaceElement> allSingles = new HashMap<>();

    static {
        allSingles.put(Java8.JAVA_TYPE, null);
    }

    @Override
    public WorkspaceElement build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        WorkspaceElement instance = getSingleOrCreate(workspace, folder, instanceType);

        for (Node propertyTypeNode : instanceNode.getChildren()) {
            if (propertyTypeNode.getArtifact().getData() instanceof TypeArtefact typeArtefact) {
                typeArtefact.build(workspace, folder, propertyTypeNode, instance, writerTypeManager);
            }
        }
        return instance;
    }

    private WorkspaceElement getSingleOrCreate(Workspace workspace, Folder folder, WorkspaceElementType instanceType)
            throws TypeMangerException {

        if (instanceType == null) {
            throw new TypeMangerException("The type for the instance could not be found");
        }

        WorkspaceElement cachedInstance = allSingles.get(instanceType);
        if (cachedInstance != null) {
            return cachedInstance;
        }


        System.out.println("Creating java element " + name + " of type " + typeName);
        WorkspaceElement newInstance = workspace.createWorkspaceElement(instanceType, name, folder);

        if (allSingles.containsKey(instanceType)) {
            allSingles.put(instanceType, newInstance);
        }

        Logger.log("Instance created: new id is " + newInstance.getId(), newInstance);
        return newInstance;
    }
}
