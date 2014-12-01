package com.enonic.wem.export.internal.reader;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ExportReader
{
    public Stream<Path> getChildrenPaths( final Path parent );

    public String getItem( final Path path );

}
