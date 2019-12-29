package com.enonic.xp.vfs;

import java.nio.file.Path;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class VirtualFilePaths
{
    public static VirtualFilePath from( final String path, final String separator )
    {
        return new VirtualFilePathImpl( path, separator );
    }

    public static VirtualFilePath from( final Path path )
    {
        final String separator = path.getFileSystem().getSeparator();

        if ( path.isAbsolute() && !path.startsWith( separator ) )
        {
            return new VirtualFileNonSlashAbsolutePath( path );
        }
        else
        {
            return new VirtualFilePathImpl( path );
        }
    }
}
