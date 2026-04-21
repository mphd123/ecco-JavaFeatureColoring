package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Instance;
import at.jku.isse.designspace.core.model.Workspace;

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

    public Optional<Instance> getClosestInstance(Folder folder ) {
        // revisit not sure about getInstances workspace
        Optional<Instance> result  = checkInstances(folder);
        searchedFolders.add(folder);
        if(result.isPresent()) return result;
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

    private Optional<Instance> checkInstances(Folder folder) {
        for (Instance otherInstance : folder.getInstances(workspace)) {
            if (newToOriginalId.containsKey(otherInstance.getId()) && Objects.equals(newToOriginalId.get(otherInstance.getId()), originalId)){
                return Optional.of(otherInstance);
            }
        }
        return Optional.empty();
    }

    private Optional<Instance> checkSubFolders(Folder folder){
        Optional<Instance> result = checkInstances(folder);
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
