package com.enonic.wem.export.internal.writer;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class VerifiableExportWriter
    implements ExportWriter
{
    private final Map<ExportItemPath, String> exportedItems = Maps.newHashMap();

    private final List<ExportItemPath> writtedPaths = Lists.newLinkedList();

    @Override
    public void createDirectory( final ExportItemPath path )
    {
        writtedPaths.add( path );
    }

    @Override
    public void writeElement( final ExportItemPath path, final String export )
    {
        exportedItems.putIfAbsent( path, export );
    }

    public Map<ExportItemPath, String> getExportedItems()
    {
        return exportedItems;
    }

    public List<ExportItemPath> getWrittedPaths()
    {
        return writtedPaths;
    }
}
