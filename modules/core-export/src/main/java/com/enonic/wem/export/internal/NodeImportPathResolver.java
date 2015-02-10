package com.enonic.wem.export.internal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.export.util.PathUtils;

class NodeImportPathResolver
{
    public static NodePath resolveNodeImportPath( final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot )
    {
        final Path parentPath = Paths.get( PathUtils.removeLeadingWindowsSlash( parent.getPath() ) );

        final Path exportRootPath = Paths.get( PathUtils.removeLeadingWindowsSlash( exportRoot.getPath() ) );

        final Path relativePath = exportRootPath.relativize( parentPath );

        final List<String> relativePathElements = PathUtils.getPathElements( relativePath.toString() );

        final NodePath.Builder builder = NodePath.newPath( importRoot );

        relativePathElements.forEach( builder::addElement );

        return builder.build();
    }
}