package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;

import java.util.*;

public class RefIdSearcher {

    final Workspace workspace;
    final Long originalId;
    final Map<Long,Long> newToOriginalId;
    // folders that are searched i nthe parent chain
    private final Set<Folder> searchedFolders = new HashSet<>();

    public RefIdSearcher(Workspace workspace, Long originalId, Map<Long, Long> newToOriginalId) {
        this.workspace = workspace;
        this.originalId = originalId;
        this.newToOriginalId = Map.copyOf(newToOriginalId);
    }

    public Optional<WorkspaceElement> getClosestInstance(Folder folder ) {
        // revisit not sure about getInstances workspace
        Optional<WorkspaceElement> result  = checkInstances(folder);
        searchedFolders.add(folder);
        if(result.isPresent()) return result;
        if(folder.getParentFolder()== null) return result;
        result = getClosestInstance(folder.getParentFolder());
        if(result.isPresent()) return result;

        // in the case where the to ref value is not in this folder and not directly in the parent folder chain
        // check in the subFolders
        for (Folder subFolder : folder.getSubFolders()) {
            if(searchedFolders.contains(subFolder)) continue;
            result = checkSubFolders(subFolder);
            if(result.isPresent()) return result;
        }


        return result;
    }

    private Optional<WorkspaceElement> checkInstances(Folder folder) {
        for (WorkspaceElement otherInstance : folder.getWorkspaceElementContents(workspace)) {
            if (newToOriginalId.containsKey(otherInstance.getId()) && Objects.equals(newToOriginalId.get(otherInstance.getId()), originalId)){
                return Optional.of(otherInstance);
            }
        }
        return Optional.empty();
    }

    private Optional<WorkspaceElement> checkSubFolders(Folder folder){
        Optional<WorkspaceElement> result = checkInstances(folder);
        if(result.isPresent()) return result;

        for (Folder subFolder : folder.getSubFolders()) {
            if(!searchedFolders.contains(subFolder)) {
                result = checkSubFolders(subFolder);
                if(result.isPresent()) return result;
            }

        }
        return result;
    }

}
