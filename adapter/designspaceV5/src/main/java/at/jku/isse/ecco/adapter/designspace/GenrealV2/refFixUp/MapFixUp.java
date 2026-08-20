package at.jku.isse.ecco.adapter.designspace.GenrealV2.refFixUp;


import at.jku.isse.designspace.commons.Key;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.artefacts.ReferenceArtefact;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;


import java.util.*;

public class MapFixUp extends AbstractRefFixUp {
    private final Map<Key, ReferenceArtefact> refMap;

    public MapFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType, Map<Key, ReferenceArtefact> refMap) {
        super(instance, propertyType);

        this.refMap = refMap;
    }

    @Override
    public void fixUp(Workspace workspace,  Set<WorkspaceElement> elements) {
        StringBuilder debuginfo = new StringBuilder("RefFixupCollection[type = " + propertyType.getName() + " / cad " + propertyType.cardinality + " ]").append("\n");

        refMap.forEach((key, referenceArtefact) -> {

            List<WorkspaceElement> matchingElements = elements.stream().filter(workspaceElement -> workspaceElement.getName().equals(referenceArtefact.name) && workspaceElement.getInstanceOf().getQualifiedName().equals(referenceArtefact.typeName)).toList();
            if(matchingElements.isEmpty()){
                System.err.println("No matching elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
                return;
            } else if(matchingElements.size() != 1){
                System.err.println("multiple elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
            }
            owningElement.add(propertyType, key, matchingElements.getFirst());
        });

        Logger.log(debuginfo.toString(), owningElement);
    }
}
