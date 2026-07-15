package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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


    public Optional<WorkspaceElement> search(Folder folder) {
        List<Long> relevantIds = new ArrayList<>();
        newToOriginalId.forEach((k,v)->{
            if (v.equals(originalId)) {
            relevantIds.add(k);
            }
        });

        AtomicLong directFound = new AtomicLong(-1);

        TreeMap<Integer,Long> parentSteps = new TreeMap<>(); 
        relevantIds.forEach((id) -> {
            WorkspaceElement element = workspace.getWorkspaceElement(id);
            Folder elementFolder = element.getFolder();
            if (elementFolder == folder) {
                if(directFound.get() != -1) throw new RuntimeException("in Folder" + folder + "there are at least two elemnts who with id mapper share the original id ");;
                directFound.set(id);
                return;
            }
            Folder searchFolder = folder;
            int steps = 0;
            while (searchFolder != null) {
                if (searchFolder.equals(elementFolder)) {
                    Long nullCHeck = parentSteps.put(steps, id);
                    if(nullCHeck != null) throw new RuntimeException("in Folder" + searchFolder + "there are at least two elemnts who with id mapper share the original id ");
                }
                searchFolder = searchFolder.getParentFolder();
                steps++;
            }
        });
        if (directFound.get() != -1) {
            Logger.log("Found existing element with id " + directFound.get() + " original Element was " + workspace.getWorkspaceElement(originalId)
            + " found element is  " + workspace.getWorkspaceElement(directFound.get()).toString());
            return Optional.of(workspace.getWorkspaceElement(directFound.get()));

        }
        if(!parentSteps.isEmpty()) return Optional.of(workspace.getWorkspaceElement(parentSteps.keySet().stream().findFirst().get()));
        // search in child Folders
        else return Optional.empty();

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
