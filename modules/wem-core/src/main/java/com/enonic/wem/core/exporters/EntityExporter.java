package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.Path;

public interface EntityExporter<T>
{
    Path exportToZip( final T object, final Path targetDirectory )
        throws IOException;

    Path exportToDirectory( final T object, final Path targetDirectory )
        throws IOException;

    T importFromZip( final Path zipFile )
        throws IOException;

    T importFromDirectory( final Path directoryPath )
        throws IOException;
}
