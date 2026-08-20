package at.jku.isse.ecco.adapter.designspace.GenrealV2.refFixUp;


import at.jku.isse.designspace.commons.Cardinality;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.artefacts.ReferenceArtefact;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;

import java.util.*;

public class CollectionFixUp extends AbstractRefFixUp {
    public final Collection<ReferenceArtefact> refCollection;

    public CollectionFixUp(WorkspaceElement instance, WorkspacePropertyType propertyType, Collection<ReferenceArtefact> refCollection) {
        super(instance, propertyType);
        this.refCollection = refCollection;
    }

    @Override
    public void fixUp(Workspace workspace, Set<WorkspaceElement> elements)  {
        Collection<WorkspaceElement> refInstances;

        if (propertyType.getCardinality().equals(Cardinality.UNORDERED_SET)) refInstances = new HashSet<>();
        else if (propertyType.getCardinality().equals(Cardinality.LIST) || propertyType.getCardinality().equals(Cardinality.ORDERED_SET))
            refInstances = new ArrayList<>();
        else throw new RuntimeException("CollectionFixUp received invalid Cardinality");
        StringBuilder debuginfo = new StringBuilder("RefFixupCollection[type = " + propertyType.getName() + " / cad " + propertyType.cardinality + " ]").append("\n");


        for (ReferenceArtefact referenceArtefact : refCollection) {

            List<WorkspaceElement> matchingElements = elements.stream().filter( workspaceElement -> workspaceElement.getName().equals(referenceArtefact.name) && workspaceElement.getInstanceOf().getQualifiedName().equals(referenceArtefact.typeName)).toList();
            if(matchingElements.isEmpty()){
                System.err.println("No matching elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
                continue;
            } else if(matchingElements.size() != 1){
                System.err.println("multiple elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
            }
            refInstances.add(matchingElements.getFirst());
        }
        Logger.log(debuginfo.toString(), owningElement);
        owningElement.setAll(propertyType, refInstances);
    }
}
