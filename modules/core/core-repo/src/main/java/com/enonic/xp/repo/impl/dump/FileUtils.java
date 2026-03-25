package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils
{
    private FileUtils()
    {
    }

    public static boolean isVisibleDirectory( final Path path )
    {
        try
        {
            return Files.isDirectory( path ) && !Files.isHidden( path );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
