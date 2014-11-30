package com.enonic.wem.export.internal.writer;

public interface ExportWriter
{
    public void createDirectory( final ExportItemPath path );

    public void writeElement( final ExportItemPath path, final String export );

}
