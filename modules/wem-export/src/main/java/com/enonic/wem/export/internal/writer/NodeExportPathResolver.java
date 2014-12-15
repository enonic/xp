package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.util.BinaryReference;

public class NodeExportPathResolver
{
    public static final String SYSTEM_FOLDER_NAME = "_";

    public static final String NODE_XML_EXPORT_NAME = "node.xml";

    public static final String BINARY_FOLDER = "bin";

    public static final String ORDER_EXPORT_NAME = "manualChildOrder.txt";

    public static Path resolveExportTargetPath( final Path basePath, final String exportName )
    {
        return Paths.get( basePath.toString(), exportName );
    }

    public static Path resolveNodeBasePath( final Path rootPath, final NodePath nodePath, final NodePath exportNodePathRoot )
    {
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

    public static Path resolveBinaryPath( final Path basePath, final BinaryReference binaryReference )
    {
        return Paths.get( basePath.toString(), BINARY_FOLDER, binaryReference.toString() );
    }
}
