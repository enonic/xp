package com.enonic.wem.api.vfs;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DriveLetterPath
    extends VirtualFilePathImpl
{
    public DriveLetterPath( final Path path )
    {
        super( path );
    }

    @Override
    public Path toLocalPath()
    {
        Path path = Paths.get( "" );

        for ( final String element : this.elements )
        {
            path = Paths.get( path.toString(), element );
        }

        return path;
    }

}
