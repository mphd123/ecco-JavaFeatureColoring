package at.jku.isse.ecco.adapter.designspace.artifact.Properties;

import at.jku.isse.ecco.adapter.designspace.artifact.SimpleValueArtifact;

public class ReferenceValueArtefact extends SimpleValueArtifact<Long> {

    public final String valueType = "ReferenceValue";

    public String getInstanceName() {
        return instanceName;
    }

    private final String instanceName;

    public ReferenceValueArtefact(Long value,String instanceName) {
        super(value);
        this.instanceName = instanceName;
    }
}
