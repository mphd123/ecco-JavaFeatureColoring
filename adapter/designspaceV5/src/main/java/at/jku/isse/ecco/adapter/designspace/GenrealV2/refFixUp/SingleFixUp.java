package at.jku.isse.ecco.adapter.designspace.GenrealV2.refFixUp;


import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.designspace.core.model.WorkspaceElement;
import at.jku.isse.designspace.core.model.WorkspacePropertyType;
import at.jku.isse.ecco.adapter.designspace.GenrealV2.artefacts.ReferenceArtefact;
import at.jku.isse.ecco.adapter.designspace.util.Logger;
import at.jku.isse.ecco.adapter.designspace.util.RefIdSearcher;
import at.jku.isse.ecco.adapter.designspace.util.WriterTypeManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SingleFixUp extends AbstractRefFixUp {
    public final ReferenceArtefact referenceArtefact;


    public SingleFixUp(WorkspaceElement owningElement, WorkspacePropertyType propertyType, ReferenceArtefact referenceArtefact) {
        super(owningElement, propertyType);

        this.referenceArtefact = referenceArtefact;
    }


    @Override
    public void fixUp(Workspace workspace, Set<WorkspaceElement> elements ) {


        List<WorkspaceElement> matchingElements = elements.stream().filter( workspaceElement -> workspaceElement.getName().equals(referenceArtefact.name) && workspaceElement.getInstanceOf().getQualifiedName().equals(referenceArtefact.typeName)).toList();

       if(matchingElements.isEmpty()){
           System.err.println("No matching elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
           return;
       } else if(matchingElements.size() != 1){
           System.err.println("multiple elements found for SingleFixup of " + owningElement.getName() + "tried to search for name" + referenceArtefact.name +" and Type" + referenceArtefact.typeName);
       }

        Logger.log("RefFixupSingle[type = " + propertyType.getName());

        // with opposed properties when setting theirs it can cause all opposed to be set on it and i think it happens when it has already been set
        if (owningElement.get(propertyType) != null) {
            Logger.log("Instance " + owningElement + "for property" + propertyType + " already has a value assumed from a different opposing property value was " + owningElement.get(propertyType));
        }

        owningElement.set(propertyType,matchingElements.getFirst());
        Logger.log("detail after set \n" + WriterTypeManager.detailRepresentation(owningElement));


    }
}
