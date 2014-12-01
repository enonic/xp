package com.enonic.wem.export.internal.reader;

import java.io.IOException;
import java.nio.file.Files;

import com.enonic.wem.export.ImportNodeException;
import com.enonic.wem.export.internal.writer.ExportItemPath;
import com.enonic.wem.export.internal.writer.ExportItemPaths;

public class FileExportReader
    implements ExportReader
{

    @Override
    public ExportItemPaths getChildrenPaths( final ExportItemPath parent )
    {
        final ExportItemPaths.Builder pathsBuilder = ExportItemPaths.create();

        try
        {
            Files.list( parent.getPath() ).
                forEach( ( path ) -> pathsBuilder.add( ExportItemPath.from( path.toString() ) ) );
        }
        catch ( IOException e )
        {
            throw new ImportNodeException( "Fetching children of parent item with path " + parent.getPathAsString() + " failed", e );
        }

        return pathsBuilder.build();
    }

    @Override
    public String getItem( final ExportItemPath path )
    {
        try
        {
            return new String( Files.readAllBytes( path.getPath() ) );
        }
        catch ( IOException e )
        {
            throw new ImportNodeException( "Could not read item with path " + path.getPathAsString(), e );
        }
    }
}
