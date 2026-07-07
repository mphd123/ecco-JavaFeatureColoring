package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.RefFixUpInterFace;
import at.jku.isse.ecco.core.Association;

import java.util.*;
import java.util.stream.Collectors;

public class WriterTypeManager {
    public final Map<Long, WorkspaceElementType> instanceTypeMap = new HashMap<>();
    public final Map<Long, WorkspacePropertyType> propertyTypeMap = new HashMap<>();

    public final Map<Long,Long> newToOriginalId = new HashMap<>();
    public final Map<Long, Association> originalIdToAssociation = new HashMap<>();
    public final Set<WorkspaceElement> previousInstances = new HashSet<>();
    public final Set<RefFixUpInterFace> refFixUps = new HashSet<>();
    public final Set<WorkspaceElement> createdInstances = new HashSet<>();

    public WriterTypeManager(Workspace workspace) {

        Set<LanguageWorkspace> languageWorkspaces = getAllLanguageWorkspaces();

        Set<WorkspacePropertyType> existingPropertyTypes =  new HashSet<>();
        Set<WorkspaceElementType> existingInstanceTypes =  new HashSet<>();
        languageWorkspaces.stream().flatMap( languageWorkspace -> languageWorkspace.getElementTypes().stream()).forEach(workspaceElementType -> {
            existingPropertyTypes.addAll(workspaceElementType.getAllPropertyTypes());
            existingInstanceTypes.add(workspaceElementType);

        });


        fillMap(instanceTypeMap,existingInstanceTypes);
        fillMap(propertyTypeMap,existingPropertyTypes);

        getAllPreviousInstances(workspace,DesignSpace.ROOT_FOLDER);


    }

    private <T extends WorkspaceElement> void fillMap(Map<Long,  T> map, Set<T> set){
        for (T type : set){
            map.put(type.getId(),type);
        }
    }

    private Set<LanguageWorkspace> getAllLanguageWorkspaces(){
        Set<LanguageWorkspace>  set = new HashSet<>();
        recursiveGetLeafWorkspace(set,DesignSpace.ROOT_LANGUAGE);
        return set;
    }

    private void recursiveGetLeafWorkspace(Set<LanguageWorkspace>  set, LanguageWorkspace workspace){
        if (workspace.getAllChildWorkspaces().isEmpty()){
            set.add(workspace);
        }else{
            for (Workspace workspace1 : workspace.getAllChildWorkspaces()){
                if (workspace1 instanceof LanguageWorkspace languageWorkspace) {
                    recursiveGetLeafWorkspace(set,languageWorkspace);
                }

            }
        }
    }

    private void getAllPreviousInstances( Workspace workspace,Folder folder) {
       previousInstances.addAll( folder.getWorkspaceElementContents(workspace));
       folder.getSubFolders().forEach(subfolder -> getAllPreviousInstances(workspace,subfolder));
    }

    private Set<WorkspaceElement> getCreatedInstancesOfId(Long originalID,Workspace workspace) {
        // idea for this is to collect all created Instances so that the ref can choose the closest

        Set<WorkspaceElement>  res = new HashSet<>();
        for (Map.Entry<Long,Long> entry : newToOriginalId.entrySet()){
            if (Objects.equals(entry.getValue(), originalID)) {
                res.addAll(createdInstances.stream().filter(instance -> instance.getId() == entry.getKey()).collect(Collectors.toSet()));
            }
        }
        // case res no instances where created
        if (res.isEmpty()) res.addAll(DesignSpace.ROOT_FOLDER.getWorkspaceElementContents(workspace).stream().filter(instance -> instance.getId() == originalID).collect(Collectors.toSet()));

        return  res;
    }

    public void resolveRefProperties(Workspace workspace){
        refFixUps.forEach(refFixUpInterFace -> refFixUpInterFace.fixUp(workspace,newToOriginalId));
    }


}
