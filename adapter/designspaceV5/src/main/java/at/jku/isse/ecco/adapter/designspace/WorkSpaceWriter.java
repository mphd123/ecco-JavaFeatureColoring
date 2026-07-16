package at.jku.isse.ecco.adapter.designspace;
import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.EccoException;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.artifact.CommitFolderArtefact;
import at.jku.isse.ecco.adapter.designspace.artifact.FolderArtefact;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;
import at.jku.isse.ecco.service.listener.WriteListener;
import at.jku.isse.ecco.tree.Node;

import java.nio.file.Path;
import java.util.*;

public class WorkSpaceWriter implements ArtifactWriter<Set<Node>, DesignSpaceInfo> {
    private final List<WriteListener> listeners = new ArrayList<>();


    @Override
    public String getPluginId() {
        return  new DesignSpacePlugin().getPluginId();
    }

    @Override
    public DesignSpaceInfo[] write(DesignSpaceInfo info, Set<Node> input) {
        Workspace workspace = info.workspace();
        Folder checkoutFolder = info.folder(); // workspace.its();
        WriterTypeManager writerTypeManager = new WriterTypeManager(workspace);
        Node pluginNode = input.stream().findFirst().orElse(null);
        if (pluginNode == null) throw new EccoException("the Workspace writer received an empty Node set");
        try {
            info.checkIfInfoValid(info);

        for (Node node : pluginNode.getChildren()){
            if(node.getArtifact().getData() instanceof CommitFolderArtefact folderArtefact){
                   folderArtefact.buildFolder(workspace,checkoutFolder,node,writerTypeManager);
            }
        }

        workspace.acceptAllChanges();
        workspace.conclude();
        Logger.enabled = true;
        writerTypeManager.resolveRefProperties(workspace);
        System.out.println("Debug ---------- FixupRecord ----------- \n" + writerTypeManager.FixupReport());

        } catch (Exception e) {
            e.printStackTrace();
            //workspace.dismissChanges();
            throw new RuntimeException(e);
        }

        workspace.acceptAllChanges();
        //workspace.conclude();

        writerTypeManager.newToOriginalId.forEach((newId, OldId) -> info.idMapper().putIds(newId, OldId) );


        listeners.forEach(listener -> listener.fileWriteEvent(Path.of(checkoutFolder.getQualifiedName()),this));
        return new DesignSpaceInfo[0];
    }


    @Override
    public DesignSpaceInfo[] write(Set<Node> input){
        throw new RuntimeException("write(Set<Node> input) is not implemented ");
        //return new Pair[0];
    }

    @Override
    public void addListener(WriteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(WriteListener listener) {
        listeners.remove(listener);
    }

}
