package com.enonic.xp.core.impl.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.api.vfs.VirtualFilePath;

class NodeImportPathResolver
{
    public static NodePath resolveNodeImportPath( final VirtualFile parent, final VirtualFile exportRoot, final NodePath importRoot )
    {
        final VirtualFilePath parentPath = parent.getPath();

        final VirtualFilePath exportRootPath = exportRoot.getPath();

        final VirtualFilePath relativePath = parentPath.subtractPath( exportRootPath );

        final NodePath.Builder builder = NodePath.newPath( importRoot );

        relativePath.getElements().forEach( builder::addElement );

        return builder.build();
    }
}