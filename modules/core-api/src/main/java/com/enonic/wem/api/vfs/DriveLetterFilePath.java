package com.enonic.wem.api.vfs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class DriveLetterFilePath
    extends VirtualFilePath
{
    public DriveLetterFilePath( final LinkedList<String> elements )
    {
        super( elements, true );
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
