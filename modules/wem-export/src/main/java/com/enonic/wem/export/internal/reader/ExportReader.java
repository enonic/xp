package com.enonic.wem.export.internal.reader;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface ExportReader
{
    public Stream<Path> getChildrenPaths( final Path parent );

    public String readItem( final Path path );

    public File getFile( final Path path );

}
