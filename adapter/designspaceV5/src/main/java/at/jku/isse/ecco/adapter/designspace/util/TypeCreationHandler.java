package at.jku.isse.ecco.adapter.designspace.util;

import at.jku.isse.designspace.core.model.InstanceType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class TypeCreationHandler {

    private final SortedMap<InstanceType,Long>  toCreateTypes = new TreeMap<>(new Comparator<InstanceType>() {
        @Override
        public int compare(InstanceType o1, InstanceType o2) {
            return 0;
        }
    });


}
