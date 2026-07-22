package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.domains.Java8;
import at.jku.isse.ecco.adapter.designspace.Java.TreeLogger;
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
        initSingleTypes();
    }

    public static void reset() {
        allSingles.clear();
        initSingleTypes();
    }

    private static void initSingleTypes() {
        allSingles.put(Java8.JAVA_TYPE, null);
    }

    @Override
    public WorkspaceElement build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        WorkspaceElement instance = getSingleOrCreate(workspace, folder, instanceType,instanceNode,writerTypeManager);


        return instance;
    }

    private WorkspaceElement getSingleOrCreate(Workspace workspace, Folder folder, WorkspaceElementType instanceType, Node instanceNode, WriterTypeManager writerTypeManager)
            throws TypeMangerException {

        if (instanceType == null) throw new TypeMangerException("Type not found");

        WorkspaceElement cachedInstance = allSingles.get(instanceType);
        if (cachedInstance != null) {
            TreeLogger.log("[Reused Singleton] " + typeName);
            return cachedInstance;
        }

        // Enter scope for newly created singleton
        try (var scope = TreeLogger.enter("SingleJavaElement [" + typeName + "] name: '" + name + "'")) {
            WorkspaceElement newInstance = workspace.createWorkspaceElement(instanceType, name, folder);

            if (allSingles.containsKey(instanceType)) {
                allSingles.put(instanceType, newInstance);
            }

            for (Node propertyTypeNode : instanceNode.getChildren()) {
                if (propertyTypeNode.getArtifact().getData() instanceof TypeArtefact typeArtefact) {
                    typeArtefact.build(workspace, folder, propertyTypeNode, newInstance, writerTypeManager);
                }
            }
            return newInstance;
        }
    }
}
