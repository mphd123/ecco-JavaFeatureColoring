package at.jku.isse.ecco;


import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.core.Checkout;
import at.jku.isse.ecco.feature.Configuration;
import at.jku.isse.ecco.service.EccoService;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Set;

public class DesignSpaceService extends EccoService {

    @Inject
    private Set<ArtifactReader<DesignSpaceInfo, Set<Node.Op>>> readers;

    private ArtifactReader<DesignSpaceInfo, Set<Node.Op>> reader;

    @Inject
    private Set<ArtifactWriter<Set<Node>, DesignSpaceInfo>> writers;

    private ArtifactWriter<Set<Node>, DesignSpaceInfo> writer;

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
        return this.reader.read(new DesignSpaceInfo(workspace,folder,null),null);
    }

    public synchronized void open(){
        super.open();
        initAdapter();
    }

    private void initAdapter(){
        reader = readers.stream().findFirst().get();
        writer = writers.stream().findFirst().get();

    }

    public synchronized Checkout checkoutDesignspace(String  configurationString,HashMap<Long,Long> newToOriginalId) {
        Configuration configuration = parseConfigurationString(configurationString);
        Checkout checkout = compose(configuration);

        Set<Node> nodes = compareArtifacts(checkout);
        this.writer.write(new DesignSpaceInfo(workspace,folder, newToOriginalId), nodes);
        return checkout;

    }

}
