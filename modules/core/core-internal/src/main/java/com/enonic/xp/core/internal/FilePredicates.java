package com.enonic.xp.core.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePredicates
{
    private FilePredicates()
    {
    }

    public static boolean isVisible( final Path file )
    {
        try
        {
            return !Files.isHidden( file );
        }
        catch ( IOException e )
        {
            return false;
        }
    }

}
