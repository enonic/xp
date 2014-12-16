package com.enonic.wem.export.internal.reader;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.io.ByteSource;

public interface ExportReader
{
    public Stream<Path> getChildrenPaths( final Path parent );

    public String readItem( final Path path );

    public List<String> readLines( final Path path );

    public File getFile( final Path path );

    public ByteSource getSource( final Path path );

}
