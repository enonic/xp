package com.enonic.xp.core.impl.export;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;

class NodeImportPathResolver
{
    public static NodePath resolveNodeImportPath( final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot )
    {
        final VirtualFilePath parentPath = parent.getPath();

        final VirtualFilePath exportRootPath = exportRoot.getPath();

        final VirtualFilePath relativePath = parentPath.subtractPath( exportRootPath );

        final NodePath.Builder builder = NodePath.create( importRoot );

        relativePath.getElements().forEach( builder::addElement );

        return builder.build();
    }
}