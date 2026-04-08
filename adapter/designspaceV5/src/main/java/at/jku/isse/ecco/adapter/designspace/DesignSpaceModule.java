package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.designspace.core.model.Element;
import at.jku.isse.designspace.core.model.Folder;
import at.jku.isse.designspace.core.model.Workspace;
import at.jku.isse.ecco.adapter.*;
import at.jku.isse.ecco.adapter.designspace.artifact.Pair;
import at.jku.isse.ecco.tree.*;
import com.google.inject.*;
import com.google.inject.multibindings.*;


import java.util.*;

public class DesignSpaceModule extends AbstractModule {

    @Inject
    public DesignSpaceModule() {    }

    @Override
    protected void configure() {
        super.configure();

        final Multibinder<ArtifactReader<Pair, Set<Node.Op>>> readerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        readerMultibinder.addBinding().to(WorkSpaceReaderNode.class);

        final Multibinder<ArtifactWriter<Set<Node>, Pair>> writerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        writerMultibinder.addBinding().to(WorkSpaceWriter.class);
    }
}
