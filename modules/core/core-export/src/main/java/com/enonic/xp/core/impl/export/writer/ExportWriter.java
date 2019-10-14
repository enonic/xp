package com.enonic.xp.core.impl.export.writer;

import java.nio.file.Path;

import com.google.common.io.ByteSource;

public interface ExportWriter
{
    void createDirectory( final Path path );

    void writeElement( final Path path, final String export );

    void writeSource( final Path itemPath, final ByteSource source );


}
