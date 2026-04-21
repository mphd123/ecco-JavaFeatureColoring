package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.artifact.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Objects;

public abstract class PropertyArtefact implements PropertyArtefactInterface {
    protected final Long id;

    protected final String name;
    protected final Cardinality cardinality;


    public PropertyArtefact(Long id, String name, Cardinality cardinality) {
        this.id = id;
        this.name = name;
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

    protected  void addValueNode(Node.Op propertyNode, Object value,EntityFactory entityFactory){
        if (value instanceof Instance instanceValue) {
            propertyNode.addChild(entityFactory.createNode(new ReferenceValueArtefact(instanceValue.getId(), instanceValue.getName())));
        }else{
            propertyNode.addChild(entityFactory.createNode(new SimpleValueArtifact<>(value)));
        }
    }
    protected   ValueArtefact<?> getValueArtefact(Node valueNode) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        if (valueNode.getArtifact().getData() instanceof ReferenceValueArtefact) {
            throw new  ExecutionControl.NotImplementedException("references");
        }else if (valueNode.getArtifact().getData() instanceof ValueArtefact<?> valueartefact) {
            return valueartefact;
        } else {
            throw new NodeWrongArtefact(" wrong artefact for value");
        }
    }
}
