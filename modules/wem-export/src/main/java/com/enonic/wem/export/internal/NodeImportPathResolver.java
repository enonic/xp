package com.enonic.wem.export.internal;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.api.vfs.VirtualFile;

class NodeImportPathResolver
{
    public static NodePath resolveNodeImportPath( final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot )
    {
        try
        {
            final Path parentPath = Paths.get( parent.getUrl().toURI() );

            final Path exportRootPath = Paths.get( exportRoot.getUrl().toURI() );

            final Path relativePath = exportRootPath.relativize( parentPath );

            final NodePath.Builder builder = NodePath.newPath( importRoot );

            relativePath.forEach( ( path ) -> {
                builder.addElement( path.toString() );
            } );

            return builder.build();

        }
        catch ( URISyntaxException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}