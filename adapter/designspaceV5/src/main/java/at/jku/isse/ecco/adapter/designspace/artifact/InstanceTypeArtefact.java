package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;

import java.util.Objects;

public class InstanceTypeArtefact implements ArtifactData {
    private final String name;
    private final Long id;

    public InstanceTypeArtefact(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceTypeArtefact that = (InstanceTypeArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
