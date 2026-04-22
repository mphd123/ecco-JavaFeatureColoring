package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.adapter.designspace.util.refFixUp.RefFixUpInterFace;
import at.jku.isse.ecco.core.Association;

import java.util.*;
import java.util.stream.Collectors;

public class WriterTypeManager {
    public final Map<Long, InstanceType> instanceTypeMap = new HashMap<>();
    public final Map<Long, PropertyType> propertyTypeMap = new HashMap<>();

    public final Map<Long,Long> newToOriginalId = new HashMap<>();
    public final Map<Long, Association> originalIdToAssociation = new HashMap<>();
    public final Set<Instance> previousInstances = new HashSet<>();
    public final Set<RefFixUpInterFace> refFixUps = new HashSet<>();
    public final Set<Instance> createdInstances = new HashSet<>();

    public WriterTypeManager(Workspace workspace) {

        Set<LanguageWorkspace> languageWorkspaces = getAllLanguageWorkspaces();


        Set<InstanceType> existingInstanceTypes =  languageWorkspaces.stream().flatMap( languageWorkspace -> languageWorkspace.getInstanceTypes().stream()).collect(Collectors.toSet());
        Set<PropertyType> existingPropertyTypes =  languageWorkspaces.stream().flatMap( languageWorkspace -> languageWorkspace.getPropertyTypes().stream()).collect(Collectors.toSet());
        fillMap(instanceTypeMap,existingInstanceTypes);
        fillMap(propertyTypeMap,existingPropertyTypes);

        getAllPreviousInstances(workspace,Folder.ROOT);


    }

    private <T extends ElementType> void fillMap(Map<Long,  T> map, Set<T> set){
        for (T type : set){
            map.put(type.getId(),type);
        }
    }

    private Set<LanguageWorkspace> getAllLanguageWorkspaces(){
        Set<LanguageWorkspace>  set = new HashSet<>();
        recursiveGetLeafWorkspace(set,LanguageWorkspace.ROOT);
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
       previousInstances.addAll( folder.getInstances(workspace));
       folder.getSubFolders().forEach(subfolder -> getAllPreviousInstances(workspace,subfolder));
    }

    private Set<Instance> getCreatedInstancesOfId(Long originalID,Workspace workspace) {
        // idea for this is to collect all created Instances so that the ref can choose the closest

        Set<Instance>  res = new HashSet<>();
        for (Map.Entry<Long,Long> entry : newToOriginalId.entrySet()){
            if (Objects.equals(entry.getValue(), originalID)) {
                res.addAll(createdInstances.stream().filter(instance -> instance.getId() == entry.getKey()).collect(Collectors.toSet()));
            }
        }
        // case res no instances where created
        if (res.isEmpty()) res.addAll(Folder.ROOT.getInstances(workspace).stream().filter(instance -> instance.getId() == originalID).collect(Collectors.toSet()));

        return  res;
    }

    public void resolveRefProperties(Workspace workspace){
        refFixUps.forEach(refFixUpInterFace -> refFixUpInterFace.fixUp(workspace,newToOriginalId));
    }


}
