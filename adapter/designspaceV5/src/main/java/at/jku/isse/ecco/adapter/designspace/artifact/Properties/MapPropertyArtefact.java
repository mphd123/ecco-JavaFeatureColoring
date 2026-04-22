package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.designspace.core.foundation.Cardinality;
import at.jku.isse.designspace.core.foundation.Key;
import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.artifact.StringArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ReferenceValueArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.SimpleValueArtifact;
import at.jku.isse.ecco.adapter.designspace.artifact.value.ValueArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.NodeWrongArtefact;
import at.jku.isse.ecco.adapter.designspace.util.RefProeprtyFixupRecord;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.MapFixUp;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashMap;
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
        Map<Key,ValueArtefact<?>> map = new HashMap<>();
        for (Node keyNode : propertyNode.getChildren()) {
            ValueArtefact<?> keyArtefact = getValueArtefact(keyNode);
            ValueArtefact<?> valueArtefact = getValueArtefact(keyNode.getChildren().get(0));
            if (keyArtefact.getValue() instanceof String keyString) {
                map.put(Key.of(keyString), valueArtefact);
            }else throw new NodeWrongArtefact("the keyNode should have a value of type Key");
        }
        setMapPropValue(instance,propertyType,map,writerTypeManager);
    }


    private void setMapPropValue(Instance instance, PropertyType propertyType, Map<Key,ValueArtefact<?>> artefactMap, WriterTypeManager writerTypeManager) {
        if(artefactMap.isEmpty()) return;
        ValueArtefact<?> example = artefactMap.values().stream().findAny().orElse(null);
        if (example instanceof  ReferenceValueArtefact referenceValueArtefact) {
            Map<Key, Long> map = new HashMap<>();
            artefactMap.forEach((key, value) -> map.put(key, (Long) value.getValue()));
            writerTypeManager.refFixUps.add(new MapFixUp(instance,propertyType,map));
        }else if (example instanceof SimpleValueArtifact<?>) {
            Map<Key, Object> map = new HashMap<>();
            artefactMap.forEach((key, value) -> map.put(key, value.getValue()));
            instance.setAll(propertyType,map);
        } else throw  new RuntimeException("unexpected value");
    }
}
