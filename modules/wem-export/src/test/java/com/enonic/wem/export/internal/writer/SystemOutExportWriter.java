package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;

class SystemOutExportWriter
    implements ExportWriter
{
    @Override
    public void createDirectory( final Path rootPath )
    {
        System.out.println( "Write directory: " + rootPath );
    }

    @Override
    public void writeElement( final Path path, final String serializedNode )
    {
        System.out.println( "Write export: " + path + ":\n\r" + serializedNode );
    }
}
