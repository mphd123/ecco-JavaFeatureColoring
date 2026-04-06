package at.jku.isse.ecco;


import at.jku.isse.designspace.core.model.Change;
import at.jku.isse.designspace.core.model.Element;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.core.Checkout;
import at.jku.isse.ecco.feature.Configuration;
import at.jku.isse.ecco.service.EccoService;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Set;

public class DesignSpaceService extends EccoService {

    @Inject
    private Set<ArtifactReader<Workspace, Set<Node.Op>>> readers;

    private ArtifactReader<Workspace, Set<Node.Op>> reader;

    @Inject
    private Set<ArtifactWriter<Set<Node>, HashMap<Long, Element>>> writers;

    private ArtifactWriter<Set<Node>, HashMap<Long, Element>> writer;

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    Workspace workspace;

    @Override
    public synchronized Set<Node.Op> readFiles() {
        return this.reader.read(workspace,null);
    }

    public synchronized void open(){
        super.open();
        initAdapter();

    }

    private void initAdapter(){
        reader = readers.stream().findFirst().get();
        writer = writers.stream().findFirst().get();

    }


    public synchronized HashMap<Long, Element>[] checkoutDesignspace(String  configurationString) {
        Configuration configuration = parseConfigurationString(configurationString);
        Checkout checkout = compose(configuration);

        Set<Node> nodes = compareArtifacts(checkout);
        HashMap<Long, Element>[] elements = this.writer.write(null, nodes);
        return elements;

    }

}
