package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class VerifiableExportWriter
    implements ExportWriter
{
    private final Map<Path, String> exportedItems = Maps.newHashMap();

    private final List<Path> writtedPaths = Lists.newLinkedList();

    @Override
    public void createDirectory( final Path path )
    {
        writtedPaths.add( path );
    }

    @Override
    public void writeElement( final Path path, final String export )
    {
        exportedItems.putIfAbsent( path, export );
    }

    public Map<Path, String> getExportedItems()
    {
        return exportedItems;
    }

    public List<Path> getWrittedPaths()
    {
        return writtedPaths;
    }
}
