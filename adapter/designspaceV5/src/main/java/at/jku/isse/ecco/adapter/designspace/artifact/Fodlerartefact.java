package at.jku.isse.ecco.adapter.designspace.artifact;

import at.jku.isse.ecco.artifact.ArtifactData;
import at.jku.isse.ecco.dao.EntityFactory;
import at.jku.isse.ecco.tree.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Fodlerartefact implements ArtifactData {
    private final String name;
    public final Collection<Integer> instantTypeIds;


    public Fodlerartefact(String name) {
        this.name = name;
        instantTypeIds = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fodlerartefact that = (Fodlerartefact) o;
        return Objects.equals(name, that.name);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public String getName() {
        return name;
    }

    public static void setUpFolderNode(Node.Op folderNode, EntityFactory factory){
        folderNode.addChild(factory.createNode(new StringArtefact("types"))); // node fortypes
        folderNode.addChild(factory.createNode(new StringArtefact("folders"))); // other folders
    }

    public enum ImportnatNodes{
        Types,
        SubFolders,
    }
}
