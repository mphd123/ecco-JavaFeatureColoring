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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SingleJavaElement extends JavaElement {

    public SingleJavaElement(String name, String typeName) {
        super(name, typeName);
    }
    public static final Map<WorkspaceElementType, Map<String, WorkspaceElement>> allSingles = new HashMap<>();

    static {
        initSingleTypes();
    }

    public static void reset() {
        allSingles.clear();
        initSingleTypes();
    }

    private static void initSingleTypes() {
        allSingles.put(Java8.JAVA_TYPE, new HashMap<>());
    }

    /**
     * Checks whether a type is registered to be treated as a SingleJavaElement.
     */
    public static boolean isRegisteredSingle(WorkspaceElementType type) {
        return allSingles.containsKey(type);
    }


    private WorkspaceElement getSingleOrCreate(Workspace workspace, Folder folder, WorkspaceElementType instanceType, Node instanceNode, WriterTypeManager writerTypeManager)
            throws TypeMangerException {

        if (instanceType == null) throw new TypeMangerException("Type not found: " + typeName);

        Map<String, WorkspaceElement> instanceByName = allSingles.get(instanceType);

        if (instanceByName != null) {
            WorkspaceElement cachedInstance = instanceByName.get(this.name);
            if (cachedInstance != null) {
                TreeLogger.log("[Reused Singleton] " + typeName + " : " + name);
                return cachedInstance;
            }
        }

        try (var scope = TreeLogger.enter("SingleJavaElement [" + typeName + "] name: '" + name + "'")) {
            WorkspaceElement newInstance = workspace.createWorkspaceElement(instanceType, name, folder);

            if (instanceByName != null) {
                instanceByName.put(this.name, newInstance);
            }

            for (Node propertyTypeNode : instanceNode.getChildren()) {
                if (propertyTypeNode.getArtifact().getData() instanceof TypeArtefact typeArtefact) {
                    typeArtefact.build(workspace, folder, propertyTypeNode, newInstance, writerTypeManager);
                }
            }
            return newInstance;
        }
    }


    @Override
    public WorkspaceElement build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        WorkspaceElement instance = getSingleOrCreate(workspace, folder, instanceType,instanceNode,writerTypeManager);


        return instance;
    }

}
