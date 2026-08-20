package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspaceElementType;
import at.jku.isse.designspace.domains.Java8;
import at.jku.isse.ecco.adapter.designspace.Java.JavaWriter;
import at.jku.isse.ecco.adapter.designspace.Java.TreeLogger;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashMap;
import java.util.Map;

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


    private WorkspaceElement getSingleOrCreate(WorkspaceElementType instanceType, Node instanceNode, JavaWriter javaWriter)
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

        try (var scope = TreeLogger.enter(() -> "SingleJavaElement [" + typeName + "] name: '" + name + "'")) {
            WorkspaceElement newInstance = javaWriter.workspace.createWorkspaceElement(instanceType, name, javaWriter.checkoutFolder);

            if (instanceByName != null) {
                instanceByName.put(this.name, newInstance);
            }

            for (Node propertyTypeNode : instanceNode.getChildren()) {
                if (propertyTypeNode.getArtifact().getData() instanceof PropTypeArtefact propTypeArtefact) {
                    propTypeArtefact.build(propertyTypeNode, newInstance, javaWriter);
                }
            }
            return newInstance;
        }
    }


    @Override
    public WorkspaceElement build(Node instanceNode, JavaWriter javaWriter)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {


        JavaWriter.processCount++;
        if ( JavaWriter.processCount % 5000 == 0) {
            long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("writing " +  JavaWriter.processCount + " elements. Heap used: " + (usedMem / 1024 / 1024) + " MB");
            System.out.println("Currently writing: SingleElement " + name + " (Type: " + typeName + ")");
        }

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);


        return getSingleOrCreate(instanceType, instanceNode, javaWriter);
    }

}
