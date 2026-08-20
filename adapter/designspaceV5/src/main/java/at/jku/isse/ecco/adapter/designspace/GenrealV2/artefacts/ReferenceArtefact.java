package at.jku.isse.ecco.adapter.designspace.GenrealV2.artefacts;

import at.jku.isse.designspace.core.model.DesignSpace;
import at.jku.isse.designspace.core.model.WorkspaceElementType;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.DesignspaceWriter;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.TreeLogger;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public class ReferenceArtefact extends WorkspaceElementArtefact {
    public ReferenceArtefact(String name, String typeName) {
        super(name, typeName);
    }

    public at.jku.isse.designspace.core.model.WorkspaceElement build(Node instanceNode, DesignspaceWriter designspaceWriter)
            throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {

        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        if (instanceType == null) throw new TypeMangerException("Type not found: " + typeName);

        try (var scope = TreeLogger.enter("ReferenceElementElement [" + typeName + "] name: '" + name + "'")) {

            return null;
        }
    }
}
