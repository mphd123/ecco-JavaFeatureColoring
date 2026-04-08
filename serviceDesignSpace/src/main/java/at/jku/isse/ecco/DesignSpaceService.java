package at.jku.isse.ecco;


import at.jku.isse.designspace.core.model.Change;
import at.jku.isse.designspace.core.model.Element;
import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.Pair;
import at.jku.isse.ecco.core.Checkout;
import at.jku.isse.ecco.feature.Configuration;
import at.jku.isse.ecco.service.EccoService;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Set;

public class DesignSpaceService extends EccoService {

    @Inject
    private Set<ArtifactReader<Pair, Set<Node.Op>>> readers;

    private ArtifactReader<Pair, Set<Node.Op>> reader;

    @Inject
    private Set<ArtifactWriter<Set<Node>, Pair>> writers;

    private ArtifactWriter<Set<Node>, Pair> writer;

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private Workspace workspace;

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    private Folder folder;

    @Override
    public synchronized Set<Node.Op> readFiles() {
        return this.reader.read(new Pair(workspace,folder),null);
    }

    public synchronized void open(){
        super.open();
        initAdapter();

    }

    private void initAdapter(){
        reader = readers.stream().findFirst().get();
        writer = writers.stream().findFirst().get();

    }

    public synchronized void checkoutDesignspace(String  configurationString) {
        Configuration configuration = parseConfigurationString(configurationString);
        Checkout checkout = compose(configuration);

        Set<Node> nodes = compareArtifacts(checkout);
        this.writer.write(new Pair(workspace,folder), nodes);

    }

}
