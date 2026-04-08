package at.jku.isse.ecco.adapter.designspace.artifact;

public class ReferenceValueArtefact extends SimpleValueArtifact<Long>{

    public final String valueType = "ReferenceValue";

    private final String instanceName;

    public ReferenceValueArtefact(Long value,String instanceName) {
        super(value);
        this.instanceName = instanceName;
    }
}
