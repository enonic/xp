package com.enonic.xp.core.impl.export.writer;

import java.nio.file.Path;

import com.google.common.io.ByteSource;

public interface ExportWriter
{
    public void createDirectory( final Path path );

    public void writeElement( final Path path, final String export );

    public void writeSource( final Path itemPath, final ByteSource source );


}
