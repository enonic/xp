package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;

public interface ExportWriter
{
    public void createDirectory( final Path path );

    public void writeElement( final Path path, final String export );

}
