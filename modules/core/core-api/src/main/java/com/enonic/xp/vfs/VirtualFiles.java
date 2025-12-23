package com.enonic.xp.vfs;

import java.io.File;
import java.nio.file.Path;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class VirtualFiles
{
    private VirtualFiles()
    {
    }

    public static VirtualFile from( final Path path )
    {
        return new LocalFile( path );
    }

    public static VirtualFile from( final File file )
    {
        return from( file.toPath() );
    }
}
