package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;

import java.util.Objects;

public class SimpleValueArtifact<T> implements ValueArtefact<T> {

    private final T value;


    public final String valueType = "SimpleValue";

    public SimpleValueArtifact(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleValueArtifact<?> that = (SimpleValueArtifact<?>) o;
        return Objects.equals(value, that.value) && Objects.equals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueType);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getValueType() {
        return valueType;
    }
}
