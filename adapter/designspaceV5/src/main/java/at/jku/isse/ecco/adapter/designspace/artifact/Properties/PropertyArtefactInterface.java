package at.jku.isse.ecco.adapter.designspace.artifact.Properties;


import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspaceProperty;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

public interface PropertyArtefactInterface extends ArtifactData {
    String getName();
    Long getId();

    void createNode(Node.Op InstanceNode, EntityFactory entityFactory, WorkspaceProperty<?> property, IdMapper idMapper)throws ExecutionControl.NotImplementedException;

    void build(Node propertyNode, WorkspaceElement instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact;

}
