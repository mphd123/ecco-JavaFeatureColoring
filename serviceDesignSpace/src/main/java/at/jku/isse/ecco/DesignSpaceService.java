package at.jku.isse.ecco;


import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.ecco.IdMapper;
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

    private IdMapper idMapper;

    public DesignSpaceInfo getDesignSpaceInfo() {
        return designSpaceInfo;
    }

    public void setDesignSpaceInfo(DesignSpaceInfo designSpaceInfo) {
        this.designSpaceInfo = designSpaceInfo;
    }

    private DesignSpaceInfo designSpaceInfo;

    @Override
    public synchronized Set<Node.Op> readFiles() {
        // change to commit with designspaceInfo
        assert(designSpaceInfo != null);
        return this.reader.read(designSpaceInfo,null);
    }

    public synchronized void open(){
        super.open();
        initAdapter();
    }

    private void initAdapter(){
        reader = readers.stream().findFirst().get();
        writer = writers.stream().findFirst().get();

    }

    public synchronized Checkout checkoutDesignspace(String  configurationString, DesignSpaceInfo info) {
        assert(info != null);
        Configuration configuration = parseConfigurationString(configurationString);
        Checkout checkout = compose(configuration);
        Set<Node> nodes = compareArtifacts(checkout);
        this.writer.write(info, nodes);
        return checkout;

    }

}
