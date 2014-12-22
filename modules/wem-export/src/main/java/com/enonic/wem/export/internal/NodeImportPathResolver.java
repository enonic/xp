package com.enonic.wem.export.internal;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;

class NodeImportPathResolver
{
    public static NodePath resolveNodeImportPath( final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot )
    {
        final Path parentPath = Paths.get( parent.getUrl().getPath() );

        final Path exportRootPath = Paths.get( exportRoot.getUrl().getPath() );

        final Path relativePath = exportRootPath.relativize( parentPath );

        final NodePath.Builder builder = NodePath.newPath( importRoot );

        relativePath.forEach( ( path ) -> builder.addElement( path.toString() ) );

        return builder.build();
    }
}