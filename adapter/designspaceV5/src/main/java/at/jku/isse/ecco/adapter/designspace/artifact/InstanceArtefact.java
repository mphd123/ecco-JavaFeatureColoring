package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;

import java.util.Objects;

public class InstanceArtefact implements ArtifactData {
    private final String name;
    private final Long id;
    private final Long instanceTypeId;


    public InstanceArtefact(String name, Long id, Long instanceTypeId) {
        this.name = name;
        this.id = id;
        this.instanceTypeId = instanceTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstanceArtefact that = (InstanceArtefact) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    public Long getInstanceTypeId() {
        return instanceTypeId;
    }
}
