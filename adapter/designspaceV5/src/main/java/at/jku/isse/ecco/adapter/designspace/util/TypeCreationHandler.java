package at.jku.isse.ecco.adapter.designspace.util;


import at.jku.isse.designspace.core.model.WorkspaceElementType;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class TypeCreationHandler {

    private final SortedMap<WorkspaceElementType, Long> toCreateTypes = new TreeMap<>(new Comparator<WorkspaceElementType>() {
        @Override
        public int compare(WorkspaceElementType o1, WorkspaceElementType o2) {
            return 0;
        }
    });


}
