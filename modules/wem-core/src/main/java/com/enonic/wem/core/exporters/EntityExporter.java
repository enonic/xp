package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.Path;

public interface EntityExporter<I, O>
{
    Path exportToZip( final I object, final Path targetDirectory )
        throws IOException;

    Path exportToDirectory( final I object, final Path targetDirectory )
        throws IOException;

    O importFromZip( final Path zipFile )
        throws IOException;

    O importFromDirectory( final Path directoryPath )
        throws IOException;
}
