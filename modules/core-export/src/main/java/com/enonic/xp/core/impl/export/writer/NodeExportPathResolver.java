package com.enonic.xp.core.impl.export.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

public class NodeExportPathResolver
{
    public static final String SYSTEM_FOLDER_NAME = "_";

    public static final String NODE_XML_EXPORT_NAME = "node.xml";

    public static final String BINARY_FOLDER = "bin";

    public static final String ORDER_EXPORT_NAME = "manualChildOrder.txt";

    public static Path resolveNodeBasePath( final Path exportFilePath, final NodePath nodePath, final NodePath exportRootNodePath )
    {
        final Path fullNodePath = Paths.get( nodePath.toString() );

        final Path exportBasePath;

        if ( exportRootNodePath.equals( NodePath.ROOT ) )
        {
            exportBasePath = Paths.get( NodePath.ROOT.toString() );
        }
        else
        {
            exportBasePath = Paths.get( exportRootNodePath.getParentPath().toString() );
        }

        final Path relativePath = exportBasePath.relativize( fullNodePath );

        return Paths.get( exportFilePath.toString(), relativePath.toString() );
    }

    public static Path resolveNodeDataFolder( final Path basePath )
    {
        return Paths.get( basePath.toString(), SYSTEM_FOLDER_NAME );
    }

    public static Path resolveOrderListPath( final Path basePath )
    {
        return Paths.get( basePath.toString(), ORDER_EXPORT_NAME );
    }

    public static Path resolveNodeXmlPath( Path basePath )
    {
        return Paths.get( basePath.toString(), NODE_XML_EXPORT_NAME );
    }

    public static Path resolveBinaryPath( final Path basePath, final BinaryReference binaryReference )
    {
        return Paths.get( basePath.toString(), BINARY_FOLDER, binaryReference.toString() );
    }
}
