package com.enonic.wem.api.support.export;


import java.nio.file.Path;

public class InvalidZipFileException
    extends RuntimeException
{
    public InvalidZipFileException( final Path zipFilePath, final Exception e )
    {
        super( "Invalid zip-file [" + zipFilePath + "]: " + e.getMessage() );
    }
}
