package com.enonic.wem.export.internal.writer;

public class SystemOutExportWriter
    implements ExportWriter
{

    @Override
    public void write( final String node )
    {
        System.out.println( node );
    }
}
