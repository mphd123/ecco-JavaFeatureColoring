package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.designspace.artifact.ElementArtifact;
import at.jku.isse.ecco.adapter.designspace.exception.MultipleWorkspaceException;
import at.jku.isse.ecco.adapter.designspace.exception.NoWorkspaceException;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class WorkspaceReader implements ArtifactReader<Workspace, Set<Node.Op>> {
    private final EntityFactory entityFactory;
    private final List<ReadListener> listeners = new ArrayList<>();
    private Folder fodler;

    @Inject
    public WorkspaceReader(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public String getPluginId() {
        return new DesignSpacePlugin().getPluginId();
    }

    @Override
    public Map<Integer, String[]> getPrioritizedPatterns() {
        // Since this reader does not interact with files, there are no patterns to look for
        return Map.of();
    }

    @Override
    public Set<Node.Op> read(Workspace base, Workspace[] input) {
        if (input != null && input.length > 0) {
            throw new MultipleWorkspaceException();
        }

        if (base == null) {
            throw new NoWorkspaceException();
        }


        // node for myself i dont think this root node here is needed since  in ser Factory this is done
        /*
        public Association.Op createAssociation(Set<Node.Op> nodes) {
            checkNotNull(nodes);
            checkArgument(!nodes.isEmpty(), "Expected a non-empty set of nodes but was empty.");

            final Association.Op association = new SerAssociation();

            RootNode.Op rootNode = this.createRootNode();
            rootNode.setContainingAssociation(association);

            for (Node.Op node : nodes) {
                rootNode.addChild(node);
            }

            association.setRootNode(rootNode);

            return association;

        */
        Node.Op pluginNode = entityFactory.createNode(new ElementArtifact(null));
        List<Element> operationList = base.accessedElements.values()
                .stream()
                // HashMaps don't guarantee a consistent order, so it just feels right to sort the values
                .sorted((a, b) -> Math.toIntExact(a.getId() - b.getId()))
                .collect(Collectors.toList());
        List<Node.Op> childNodes = new ArrayList<>();

        childNodes.add(pluginNode);

        for (Element element: operationList) {
            Node.Op root = childNodes.remove(0);
            Node.Op node = entityFactory.createNode(new ElementArtifact(element));

            root.addChild(node);
            childNodes.add(node);
            childNodes.add(node);
        }

        listeners.forEach(listener -> listener.fileReadEvent(null, this));
        return Set.of(pluginNode);
    }

    @Override
    public Set<Node.Op> read(Workspace[] input) {
        if (input != null && input.length > 1) {
            throw new MultipleWorkspaceException();
        }

        if (input == null || input.length == 0) {
            throw new NoWorkspaceException();
        }

        return read(input[0], null);
    }

    @Override
    public void addListener(ReadListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ReadListener listener) {
        listeners.remove(listener);
    }
}
