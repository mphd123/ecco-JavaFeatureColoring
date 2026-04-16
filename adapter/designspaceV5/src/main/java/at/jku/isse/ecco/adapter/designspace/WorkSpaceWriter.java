package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.FolderArtefact;
import at.jku.isse.ecco.adapter.designspace.exception.FolderException;
import at.jku.isse.ecco.adapter.designspace.exception.IDMapperException;
import at.jku.isse.ecco.adapter.designspace.exception.WorkspaceException;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.service.listener.ReadListener;
import at.jku.isse.ecco.service.listener.WriteListener;
import at.jku.isse.ecco.tree.Node;

import java.util.*;

public class WorkSpaceWriter implements ArtifactWriter<Set<Node>, DesignSpaceInfo> {
    private final List<ReadListener> listeners = new ArrayList<>();


    @Override
    public String getPluginId() {
        return  new DesignSpacePlugin().getPluginId();
    }

    @Override
    public DesignSpaceInfo[] write(DesignSpaceInfo info, Set<Node> input) {
        Workspace workspace = info.workspace();
        Folder checkoutFolder = workspace.its(info.folder());
        WriterTypeManager writerTypeManager = new WriterTypeManager();
        Node pluginNode = input.stream().toList().get(0);
        try {
            checkIfValid(info);

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

        writerTypeManager.newToOriginalId.forEach((newId, OldId) -> info.idMapper().putIds(newId, OldId) );
        return new DesignSpaceInfo[0];
    }

    private void checkIfValid(DesignSpaceInfo info) {
        if (info.idMapper() == null) throw new IDMapperException("is null");
        if (info.idMapper().getCurrentRepId() == null || info.idMapper().getCurrentRepId().isBlank()) {
            throw new IDMapperException(String.format("the set repId for IDMapper is invalid is [%s]",info.idMapper().getCurrentRepId()));
        }
        if (info.workspace() == null) throw new WorkspaceException("is null");
        if (info.folder() == null) throw new FolderException("is null");
        Collection<Instance> instances = (Collection<Instance>) info.folder().get(Folder.INSTANCES);
        if(!instances.isEmpty() || !info.folder().getSubFolders().isEmpty()) throw new FolderException("the chosen Folder is not empty");
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
