package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;

import java.util.Objects;

public class StringArtefact implements ArtifactData {
    private final String key;

    public StringArtefact(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StringArtefact that = (StringArtefact) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    public String getKey() {
        return key;
    }
}
