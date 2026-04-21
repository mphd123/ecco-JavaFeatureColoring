package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.foundation.Key;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.artifact.StringArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.Map;

public class MapPropertyArtefact extends PropertyArtefact{
    public MapPropertyArtefact(Long id, String name, Cardinality cardinality) {
        super(id, name, cardinality);
    }

    public void createNode(Node.Op InstanceNode, EntityFactory entityFactory, Property property) {
        Node.Op mapNode = entityFactory.createNode(this);
        InstanceNode.addChild(mapNode);
        addMapNodes(mapNode,(MapProperty) property, entityFactory);
    }

    private  void addMapNodes(Node.Op propertyNode, MapProperty property, EntityFactory entityFactory){
        Map<?,?> map = property.get();
        for (Object key: map.keySet()){
            Object value = map.get(key);
            Node.Op keyNode = entityFactory.createNode(new StringArtefact( ((Key) key).getKey()));
            addValueNode(keyNode,value,entityFactory);
            propertyNode.addChild(keyNode);
        }
    }

    public void build(Node propertyNode, Instance instance, WriterTypeManager writerTypeManager) throws ExecutionControl.NotImplementedException, NodeWrongArtefact {
        PropertyType propertyType = instance.getPropertyType(name);
        for (Node keyNode : propertyNode.getChildren()) {
            ValueArtefact<?> keyArtefact = getValueArtefact(keyNode);
            ValueArtefact<?> valueArtefact = getValueArtefact(keyNode.getChildren().get(0));
            if (keyArtefact.getValue() instanceof String keyString) {
                instance.add(propertyType,Key.of(keyString),valueArtefact.getValue());
            }else throw new NodeWrongArtefact("the keyNode should have a value of type Key");
        }
    }
}
