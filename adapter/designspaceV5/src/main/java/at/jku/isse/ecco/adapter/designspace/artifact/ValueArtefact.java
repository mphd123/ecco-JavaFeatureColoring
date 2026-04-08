package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;

public interface ValueArtefact<T> extends ArtifactData {
    T getValue();
    String getValueType();
}
