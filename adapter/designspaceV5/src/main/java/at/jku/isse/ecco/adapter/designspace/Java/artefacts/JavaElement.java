package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspaceElementType;
import at.jku.isse.ecco.adapter.designspace.Java.JavaWriter;
import at.jku.isse.ecco.adapter.designspace.Java.TreeLogger;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public class JavaElement implements JavaArtefact {
    public final String name;
    public final String typeName;

    public JavaElement(String name, String typeName) {
        this.name = name != null ? name.intern() : null;
        this.typeName = typeName != null ? typeName.intern() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaElement that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, typeName);
    }


    public WorkspaceElement build(Node instanceNode, JavaWriter javaWriter)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {


        JavaWriter.processCount++;
        if ( JavaWriter.processCount % 5000 == 0) {
            long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("writing " +  JavaWriter.processCount + " elements. Heap used: " + (usedMem / 1024 / 1024) + " MB");
            System.out.println("Currently writing: Element " + name + " (Type: " + typeName + ")");
        }

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        if (instanceType == null) throw new TypeMangerException("Type not found: " + typeName);

        try (var scope = TreeLogger.enter(() -> "JavaElement [" + typeName + "] name: '" + name + "'")) {
            WorkspaceElement instance = javaWriter.workspace.createWorkspaceElement(instanceType, name, javaWriter.checkoutFolder);

            for (Node propertyTypeNode : instanceNode.getChildren()) {
                if (propertyTypeNode.getArtifact().getData() instanceof PropTypeArtefact propTypeArtefact) {
                    propTypeArtefact.build(propertyTypeNode, instance, javaWriter);
                }
            }
            return instance;
        }
    }
}
