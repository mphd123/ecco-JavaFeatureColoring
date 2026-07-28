package at.jku.isse.ecco.adapter.designspace.artifact.Properties;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.ecco.adapter.designspace.WorkSpaceReader;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public abstract class PropertyArtefact implements PropertyArtefactInterface {
    protected final Long id;

    protected final String name;
    protected final Cardinality cardinality;


    public PropertyArtefact(Long id, String name, Cardinality cardinality) {
        this.id = id;
        this.name = name; // name needs to be in hte from of WorkspaceElementTypeQualifiedName::
        this.cardinality = cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PropertyArtefact that = (PropertyArtefact) o;
        return Objects.equals(name, that.name) && cardinality == that.cardinality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cardinality);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    protected void addValueNode(Node.Op propertyNode, Object value, WorkSpaceReader reader) {
        if (value instanceof WorkspaceElement instanceValue) {
            Long originalId = reader.idMapper.getOriginalId(instanceValue.getId());
            propertyNode.addChild(reader.entityFactory.createNode(new ReferenceValueArtefact(originalId, instanceValue.getName(), instanceValue.getInstanceOf().getName())));
            if (originalId == null)
                Logger.log("Debug Reader AddValueNode : Value= Warning original id was null for " + instanceValue.getId());
            else Logger.log("Debug Reader AddValueNode : Value= " + originalId);


        } else {
            propertyNode.addChild(reader.entityFactory.createNode(new SimpleValueArtifact<>(value)));
            if (value == null) Logger.log("Debug Reader AddValueNode :  Value= " + null);
            else Logger.log("Debug Reader AddValueNode :  Value= " + value.toString());

        }

    }

    protected ValueArtefact<?> getValueArtefact(Node valueNode) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        if (valueNode.getArtifact().getData() instanceof ReferenceValueArtefact refArtefact) {
            return refArtefact;
        } else if (valueNode.getArtifact().getData() instanceof ValueArtefact<?> valueartefact) {
            return valueartefact;
        } else {
            throw new NodeWrongArtefact(" wrong artefact for value");
        }
    }


}
