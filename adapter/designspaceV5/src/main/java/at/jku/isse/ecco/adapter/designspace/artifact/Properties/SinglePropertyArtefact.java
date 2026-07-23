package at.jku.isse.ecco.adapter.designspace.artifact.Properties;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
import at.jku.isse.ecco.adapter.designspace.WorkSpaceReader;
import at.jku.isse.ecco.adapter.designspace.WorkSpaceWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefProeprtyFixupRecord;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.SingleFixUp;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.List;

public class SinglePropertyArtefact extends PropertyArtefact {

    public SinglePropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }


    public void createNode(Node.Op InstanceNode, WorkspaceProperty<?> property, WorkSpaceReader reader) {
        Node.Op single = reader.entityFactory.createNode(this);
        InstanceNode.addChild(single);
        addValueNode(single, property.get(), reader);


        if (property.get() == null) Logger.log("SingleProperty Value= null");
        else Logger.log("SingleProperty Value= " + property.get().toString());
    }

    public void build(Node propertyNode, WorkspaceElement instance, WorkSpaceWriter writer) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        if (propertyNode.getChildren().size() != 1) {
            System.err.println("SinglePropartefact  did not have 1 child it had =" + propertyNode.getChildren().size() );
            return;
        }
        Node valueNode = propertyNode.getChildren().get(0);

        WorkspacePropertyType propertyType =  instance.getInstanceOf().getPropertyType(name);
        setSinglePropValue(instance,propertyType,getValueArtefact(valueNode),writer);
    }

    private void setSinglePropValue(WorkspaceElement instance, WorkspacePropertyType propertyType, ValueArtefact<?> valueArtefact, WorkSpaceWriter writer) throws NodeWrongArtefact {
        if (valueArtefact instanceof  ReferenceValueArtefact referenceValueArtefact) {

            // contained elements get there prop set by the container  so skip them
            if (propertyType.isContained()) {
                return;
            }
            writer.writerTypeManager.refFixUps.add(new SingleFixUp(instance,propertyType,referenceValueArtefact.getValue()));
        }else if (valueArtefact instanceof SimpleValueArtifact<?> valueArtifact) {
            instance.set(propertyType,valueArtifact.getValue());
        } else throw  new RuntimeException("unexpected value");
    }
}
