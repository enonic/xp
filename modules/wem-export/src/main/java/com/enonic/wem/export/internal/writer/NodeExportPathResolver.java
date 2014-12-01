package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

public class NodeExportPathResolver
{

    private static final String SYSTEM_FOLDER_NAME = "_";

    private static final String NODE_XML_EXPORT_NAME = "node.xml";

    private static final String ORDER_EXPORT_NAME = "manualChildOrder.txt";

    public static Path resolveExportRoot( final Path basePath, final String exportName )
    {
        return Paths.get( basePath.toString(), exportName );
    }

    public static Path resolveExportNodeRoot( final Path rootPath, final Node node )
    {
        final NodePath nodePath = node.path();

        return Paths.get( rootPath.toString(), nodePath.asRelative().toString() );
    }

    public static Path resolveExportNodeDataPath( final Path basePath )
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
