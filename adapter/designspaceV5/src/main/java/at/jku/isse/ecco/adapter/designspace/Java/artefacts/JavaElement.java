package at.jku.isse.ecco.adapter.designspace.Java.artefacts;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.exception.InstanceTypeException;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.TypeMangerException;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public class JavaElement implements JavaArtefact {
    public final String name;
    public final String typeName;

    public JavaElement(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JavaElement that = (JavaElement) o;
        return Objects.equals(name, that.name) && Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, typeName);
    }



    public WorkspaceElement build(Workspace workspace, Folder folder, Node instanceNode, WriterTypeManager writerTypeManager) throws NodeWrongArtefact, TypeMangerException, ExecutionControl.NotImplementedException {
        WorkspaceElementType instanceType = DesignSpace.getElementType(typeName);
        if (instanceType == null) throw new TypeMangerException("the type for the Instance could not be found");
        WorkspaceElement instance = workspace.createWorkspaceElement( instanceType, name, folder);
        //writerTypeManager.newToOriginalId.put(instance.getId(), id);
        Logger.log(" instance created old id was " + "id" + "new id is " + instance.getId(), instance);

        for (Node propertyTypeNode : instanceNode.getChildren()) {
            if (propertyTypeNode.getArtifact() instanceof TypeArtefact typeArtefact){
                typeArtefact.build(workspace,folder,propertyTypeNode,instance,writerTypeManager);
            }
        }
        return instance;
    }
}
