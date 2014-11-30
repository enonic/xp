package com.enonic.wem.export.internal.writer;

public class SystemOutExportWriter
    implements ExportWriter
{
    @Override
    public void createDirectory( final ExportItemPath rootPath )
    {
        System.out.println( "Write directory: " + rootPath.getPathAsString() );
    }

    @Override
    public void writeElement( final ExportItemPath path, final String serializedNode )
    {
        System.out.println( "Path: " + path.getPathAsString() + ":\n\r" + serializedNode );
    }
}
