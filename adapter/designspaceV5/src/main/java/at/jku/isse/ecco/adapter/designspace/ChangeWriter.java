package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.Change;
import at.jku.isse.designspace.core.model.Element;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.ElementArtifact;
import at.jku.isse.ecco.service.listener.WriteListener;
import at.jku.isse.ecco.tree.Node;
import at.jku.isse.ecco.tree.RootNode;

import java.util.*;

public class ChangeWriter implements ArtifactWriter<Set<Node>, HashMap<Long, Element>> {
    private final List<WriteListener> listeners = new LinkedList<>();

    @Override
    public String getPluginId() {
        return new DesignSpacePlugin().getPluginId();
    }

    @Override
    public HashMap<Long, Element>[] write(HashMap<Long, Element> base, Set<Node> input) {
        HashMap<Long, Element> elements = new HashMap<>();
        List<Node> nodes = new ArrayList<>(input);

        while (nodes.size() > 0){
            Node node = nodes.remove(0);

            nodes.addAll(node.getChildren());

            if (node instanceof RootNode) {
                // Root node does not contain any data so it must be skipped
                continue;
            }

            if (node.getArtifact() == null) {
                // Skip nodes with invalid artifact
                continue;
            }

            if (node.getArtifact().getData() == null || !(node.getArtifact().getData() instanceof ElementArtifact)) {
                // Skip nodes with invalid data
                continue;
            }

            ElementArtifact artifact = (ElementArtifact) node.getArtifact().getData();
            Element element = artifact.getElement();

            if (element == null) {
                // Skip nodes with invalid payload
                continue;
            }

            elements.put(element.getId(), element);
        }

        listeners.forEach(listener -> listener.fileWriteEvent(null, this));

        // Linter may complain that a HashMap array without specific types is returned
        // but in Java it's not possible to create arrays with type arguments so the linter just has to live with that
        return new HashMap[]{elements};
    }

    @Override
    public HashMap<Long, Element>[] write(Set<Node> input) {
        return write(null, input);
    }

    @Override
    public void addListener(WriteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(WriteListener listener) {
        listeners.remove(listener);
    }
}
