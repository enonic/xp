package com.enonic.wem.export.internal.reader;

import java.nio.file.Path;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;

public class NodeImportPathResolver
{
    public static NodePath resolve( final Path filePath, final Path exportRootPath, final NodePath importRoot )
    {
        final Path relativeFilePath = exportRootPath.relativize( filePath );
        final Path systemFolder = relativeFilePath.getParent();

        if ( systemFolder == null )
        {
            throw new NodeImportPathException( "file found in invalid directory: " + filePath + " in export root path " + exportRootPath );
        }

        if ( !NodeExportPathResolver.SYSTEM_FOLDER_NAME.equals( systemFolder.getFileName().toString() ) )
        {
            throw new NodeImportPathException( "File found outside system-folder" + filePath.toString() );
        }

        final Path folderPath = systemFolder.getParent();

        if ( folderPath == null )
        {
            throw new NodeImportPathException( "Node-name not found in path: " + filePath + ", import root: " + exportRootPath );
        }

        return NodePath.newNodePath( importRoot, folderPath.toString() ).build().asAbsolute();
    }
}
