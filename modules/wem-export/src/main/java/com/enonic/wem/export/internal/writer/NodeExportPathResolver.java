package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.NodePath;

public class NodeExportPathResolver
{

    private static final String SYSTEM_FOLDER_NAME = "_";

    private static final String NODE_XML_EXPORT_NAME = "node.xml";

    private static final String ORDER_EXPORT_NAME = "manualChildOrder.txt";

    public static Path resolveExportTargetPath( final Path basePath, final String exportName )
    {
        return Paths.get( basePath.toString(), exportName );
    }

    public static Path resolveNodeBasePath( final Path rootPath, final NodePath nodePath, final NodePath exportNodePathRoot )
    {
        // Get path relative to export-root

        final Path fullNodePath = Paths.get( nodePath.toString() );
        final Path exportBasePath = Paths.get( exportNodePathRoot.toString() );

        final Path relativePath = exportBasePath.relativize( fullNodePath );

        return Paths.get( rootPath.toString(), relativePath.toString() );
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

}
