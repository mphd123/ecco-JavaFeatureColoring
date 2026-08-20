package at.jku.isse.ecco;


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
import java.util.Map;
import java.util.Set;

import static at.jku.isse.ecco.adapter.designspace.DesignSpaceModule.*;

public class DesignSpaceService extends EccoService {

    @Inject
    private Set<ArtifactReader<DesignSpaceInfo, Set<Node.Op>>> readers;
    private Map<DesignSpaceInfo.adapterType,ArtifactReader<DesignSpaceInfo, Set<Node.Op>> > readerMap = new HashMap<>();

    @Inject
    private Set<ArtifactWriter<Set<Node>, DesignSpaceInfo>> writers;

    private Map<DesignSpaceInfo.adapterType,ArtifactWriter<Set<Node>, DesignSpaceInfo> > writerMap = new HashMap<>();

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
        ArtifactReader<DesignSpaceInfo, Set<Node.Op>> reader = readerMap.get(designSpaceInfo.adapterType());
        assert(reader != null);
        return reader.read(designSpaceInfo,null);
    }

    public synchronized void open(){
        super.open();
        initAdapter();
    }

    private void initAdapter(){
        readers.forEach(reader -> {
            if (reader.toString().equals(javaAdpaterString)){
                readerMap.put(DesignSpaceInfo.adapterType.JAVA,reader);
            } else if (reader.toString().equals(generalAdpaterString)){
                readerMap.put(DesignSpaceInfo.adapterType.GENERAL,reader);
            }
        });

        writers.forEach(writer -> {
            if (writer.toString().equals(javaAdpaterString)){
                writerMap.put(DesignSpaceInfo.adapterType.JAVA,writer);
            } else if (writer.toString().equals(generalAdpaterString)){
                writerMap.put(DesignSpaceInfo.adapterType.GENERAL,writer);
            }else if (writer.toString().equals(generalAdpaterV2String)){
                writerMap.put(DesignSpaceInfo.adapterType.GeneralV2,writer);
            }
        });

    }

    public synchronized Checkout checkoutDesignspace(String  configurationString, DesignSpaceInfo info) {
        assert(info != null);
        designSpaceInfo = info;
        Configuration configuration = parseConfigurationString(configurationString);
        Checkout checkout = compose(configuration);
        Set<Node> nodes = compareArtifacts(checkout);

        ArtifactWriter<Set<Node>, DesignSpaceInfo> writer = writerMap.get(designSpaceInfo.adapterType());
        assert(writer != null);
        writer.write(info, nodes);
        return checkout;

    }

}
