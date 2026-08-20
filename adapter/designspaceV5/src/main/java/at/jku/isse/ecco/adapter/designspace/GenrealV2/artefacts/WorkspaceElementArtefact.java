package at.jku.isse.ecco.adapter.designspace.GenrealV2.artefacts;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.WorkspaceElementType;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.DesignspaceWriter;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.TreeLogger;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public class WorkspaceElementArtefact implements ArtifactData {
    public final String name;
    public final String typeName;

    public WorkspaceElementArtefact(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceElementArtefact that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, typeName);
    }


    public at.jku.isse.designspace.core.model.WorkspaceElement build(Node instanceNode, DesignspaceWriter designspaceWriter)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        if (instanceType == null) throw new TypeMangerException("Type not found: " + typeName);

        try (var scope = TreeLogger.enter("WorkspaceElement [" + typeName + "] name: '" + name + "'")) {
            at.jku.isse.designspace.core.model.WorkspaceElement instance = designspaceWriter.workspace.createWorkspaceElement(instanceType, name, designspaceWriter.checkoutFolder);
            designspaceWriter.createdElements.add(instance);

            for (Node propertyTypeNode : instanceNode.getChildren()) {
                if (propertyTypeNode.getArtifact().getData() instanceof PropTypeArtefact propTypeArtefact) {
                    propTypeArtefact.build(propertyTypeNode, instance, designspaceWriter);
                }
            }
            return instance;
        }
    }
}
