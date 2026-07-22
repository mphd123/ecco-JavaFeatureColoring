package at.jku.isse.ecco.adapter.designspace.Java;

import at.jku.isse.designspace.core.model.WorkspacePropertyType;

import java.util.Set;

import static at.jku.isse.designspace.domains.Java8.*;

public class Filter {


    private static final Set<WorkspacePropertyType> CONTAINMENT_WHITELIST = Set.of(

            JAVA_PACKAGE__PACKAGES,
            JAVA_PACKAGE__FILES,
            JAVA_PACKAGE__CLASSES,


            JAVA_FILE__IMPORTS,


            JAVA_CLASS__INNER_CLASSES

    );

    public static boolean isStructuralChildProperty(WorkspacePropertyType propType) {
        return CONTAINMENT_WHITELIST.contains(propType);
    }


    public static boolean shouldProcessProperty(WorkspacePropertyType propType) {
        String name = propType.getQualifiedName();

        // 1. DANGER: Always skip parent back-references.
        // In your metamodel, these are exclusively named "owner" or "try" (for catch sections).
        if (name.endsWith("::owner") || name.endsWith("__OWNER")) {
            return false;
        }
        if (name.endsWith("::try") || name.endsWith("__TRY")) {
            return false;
        }

        if (name.endsWith("::importUsages") ||
                name.endsWith("::typeUsages") ||
                name.endsWith("::subClasses") ||
                name.endsWith("::implementations") ||
                name.endsWith("::fieldTypes") ||
                name.endsWith("::methodReturnTypes") ||
                name.endsWith("::callers") ||
                name.endsWith("::callees") ||
                name.endsWith("::accesses") ||
                name.endsWith("::accessedBy") ||
                name.endsWith("::uses") ||
                name.endsWith("::methodUsages") ||
                name.endsWith("::classReference") ||
                name.endsWith("::packageReference") ||
                name.endsWith("::classReferences") ||
                name.endsWith("::parameterTypes")) {
            return false;
        }


        if (name.contains("@") || name.endsWith("::pre")) {
            return false;
        }

        return true;
    }


}
