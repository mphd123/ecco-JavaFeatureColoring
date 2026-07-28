package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.ecco.adapter.ArtifactReader;
import at.jku.isse.ecco.adapter.ArtifactWriter;
import at.jku.isse.ecco.adapter.designspace.Java.JavaReader;
import at.jku.isse.ecco.adapter.designspace.Java.JavaWriter;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
import at.jku.isse.ecco.tree.Node;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import java.util.Set;

public class DesignSpaceModule extends AbstractModule {

    public static final String javaAdpaterString = "Java8";
    public static final String generalAdpaterString = "general";

    @Inject
    public DesignSpaceModule() {
    }

    @Override
    protected void configure() {
        super.configure();

        final Multibinder<ArtifactReader<DesignSpaceInfo, Set<Node.Op>>> readerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        readerMultibinder.addBinding().to(WorkSpaceReader.class);
        readerMultibinder.addBinding().to(JavaReader.class);

        final Multibinder<ArtifactWriter<Set<Node>, DesignSpaceInfo>> writerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        writerMultibinder.addBinding().to(WorkSpaceWriter.class);
        writerMultibinder.addBinding().to(JavaWriter.class);
    }


}
