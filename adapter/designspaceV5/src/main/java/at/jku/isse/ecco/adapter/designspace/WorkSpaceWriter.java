package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.FolderArtefact;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.service.listener.WriteListener;
import at.jku.isse.ecco.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkSpaceWriter implements ArtifactWriter<Set<Node>, DesignSpaceInfo> {
    private final List<ReadListener> listeners = new ArrayList<>();


    @Override
    public String getPluginId() {
        return  new DesignSpacePlugin().getPluginId();
    }

    @Override
    public DesignSpaceInfo[] write(DesignSpaceInfo base, Set<Node> input) {
        Workspace workspace = base.workspace();
        Folder checkoutFolder = workspace.its(base.folder());
        WriterTypeManager writerTypeManager = new WriterTypeManager();
        Node pluginNode = input.stream().toList().get(0);
        try {

        for (Node node : pluginNode.getChildren()){
            if(node.getArtifact().getData() instanceof FolderArtefact){
                    FolderArtefact.buildFolder(workspace,checkoutFolder,node,writerTypeManager);
            }
        }

        } catch (Exception e) {
            workspace.dismissChanges();
            throw new RuntimeException(e);
        }
        workspace.concludeChange();
        // this gave me a null key testing why  typewritermanager did not contain a null value
        // writerTypeManager.newToOriginalId.forEach((newId, OldId) -> base.idMapper().putIds(newId, OldId) );
        for (Map.Entry<Long,Long> entry : writerTypeManager.newToOriginalId.entrySet()) {
            base.idMapper().putIds(entry.getKey(), entry.getValue());
        }




        return new DesignSpaceInfo[0];
    }

    @Override
    public DesignSpaceInfo[] write(Set<Node> input){
        throw new RuntimeException("write(Set<Node> input) is not implemented ");
        //return new Pair[0];
    }

    @Override
    public void addListener(WriteListener listener) {

    }

    @Override
    public void removeListener(WriteListener listener) {

    }
}
