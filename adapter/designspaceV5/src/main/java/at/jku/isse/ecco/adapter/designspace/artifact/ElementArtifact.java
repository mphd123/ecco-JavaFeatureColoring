package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.designspace.core.model.Element;
import at.jku.isse.ecco.artifact.ArtifactData;

import java.util.Objects;

public class ElementArtifact implements ArtifactData {
    private final Element element;

    public ElementArtifact(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementArtifact that = (ElementArtifact) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public String toString() {
        return String.format("%s[ %s ]",
                super.toString(),
                element.toString());
    }
}
