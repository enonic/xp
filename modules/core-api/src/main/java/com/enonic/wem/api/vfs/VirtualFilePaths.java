package com.enonic.wem.api.vfs;

import java.nio.file.Path;

public class VirtualFilePaths
{

    public static VirtualFilePath from( final String path, final String separator )
    {
        return new VirtualFilePathImpl( path, separator );
    }

    public static VirtualFilePath from( final Path path )
    {
        final String separator = path.getFileSystem().getSeparator();

        if ( !path.isAbsolute() && path.startsWith( separator ) )
        {
            return new DriveLetterPath( path );
        }
        else
        {
            return new VirtualFilePathImpl( path );
        }
    }


}
