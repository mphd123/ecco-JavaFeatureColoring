package at.jku.isse.ecco.adapter.designspace.artifact.value;

public class ReferenceValueArtefact extends SimpleValueArtifact<Long> {

    public final String valueType = "ReferenceValue";

    public String getInstanceTypeName() {
        return instanceTypeName;
    }

    public final String instanceTypeName;


    public String getInstanceName() {
        return instanceName;
    }

    private final String instanceName;

    public ReferenceValueArtefact(Long value, String instanceName, String finalInstanceTypeName) {
        super(value);
        this.instanceTypeName = finalInstanceTypeName;
        this.instanceName = instanceName;
    }
}
