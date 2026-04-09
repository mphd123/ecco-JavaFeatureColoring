package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.*;
import at.jku.isse.ecco.core.Association;

import java.util.*;
import java.util.stream.Collectors;

public class WriterTypeManager {
    public final Map<Long, InstanceType> instanceTypeMap;
    public final Map<Long, PropertyType> propertyTypeMap;

    public final Map<Long,Long> newToOriginalId;
    public final Map<Long, Association> originalIdToAssociation;

    public WriterTypeManager() {

        LanguageWorkspace workspace = LanguageWorkspace.ROOT;
        instanceTypeMap = new HashMap<>();
        propertyTypeMap = new HashMap<>();
        newToOriginalId = new HashMap<>();
        originalIdToAssociation = new HashMap<>();

        Set<LanguageWorkspace> languageWorkspaces = getAllLanguageWorkspaces();


        Set<InstanceType> existingInstanceTypes =  languageWorkspaces.stream().flatMap( languageWorkspace -> languageWorkspace.getInstanceTypes().stream()).collect(Collectors.toSet());
        Set<PropertyType> existingPropertyTypes =  languageWorkspaces.stream().flatMap( languageWorkspace -> languageWorkspace.getPropertyTypes().stream()).collect(Collectors.toSet());
        fillMap(instanceTypeMap,existingInstanceTypes);
        fillMap(propertyTypeMap,existingPropertyTypes);
    }

    private <T extends ElementType> void fillMap(Map<Long,  T> map, Set<T> set){
        for (T type : set){
            map.put(type.getId(),type);
        }
    }

    private Set<LanguageWorkspace> getAllLanguageWorkspaces(){
        Set<LanguageWorkspace>  set = new HashSet<>();
        recusrivegetLeafWorkspaces(set,LanguageWorkspace.ROOT);
        return set;
    }

    private void recusrivegetLeafWorkspaces(Set<LanguageWorkspace>  set,LanguageWorkspace workspace){
        if (workspace.getAllChildWorkspaces().isEmpty()){
            set.add(workspace);
        }else{
            for (Workspace workspace1 : workspace.getAllChildWorkspaces()){
                if (workspace1 instanceof LanguageWorkspace languageWorkspace)
                recusrivegetLeafWorkspaces(set,languageWorkspace);

            }
        }
    }


}
