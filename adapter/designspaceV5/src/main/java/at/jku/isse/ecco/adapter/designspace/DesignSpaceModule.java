package at.jku.isse.ecco.adapter.designspace;

import at.jku.isse.ecco.adapter.*;
import at.jku.isse.ecco.adapter.designspace.util.DesignSpaceInfo;
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

        final Multibinder<ArtifactReader<DesignSpaceInfo, Set<Node.Op>>> readerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        readerMultibinder.addBinding().to(WorkSpaceReader.class);

        final Multibinder<ArtifactWriter<Set<Node>, DesignSpaceInfo>> writerMultibinder =
                Multibinder.newSetBinder(
                        binder(),
                        new TypeLiteral<>() {
                        });

        writerMultibinder.addBinding().to(WorkSpaceWriter.class);
    }
}
