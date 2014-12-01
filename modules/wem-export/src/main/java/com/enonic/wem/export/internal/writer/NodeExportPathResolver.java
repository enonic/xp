package com.enonic.wem.export.internal.writer;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

public class NodeExportPathResolver
{

    private static final String SYSTEM_FOLDER_NAME = "_";

    private static final String NODE_XML_EXPORT_NAME = "node.xml";

    private static final String ORDER_EXPORT_NAME = "manualChildOrder.txt";

    public static ExportItemPath resolveExportRoot( final ExportItemPath basePath, final String exportName )
    {
        return ExportItemPath.from( basePath, exportName );
    }

    public static ExportItemPath resolveExportNodeRoot( final ExportItemPath rootPath, final Node node )
    {
        final NodePath nodePath = node.path();

        return ExportItemPath.from( rootPath, nodePath.asRelative().toString() );
    }

    public static ExportItemPath resolveExportNodeDataPath( final ExportItemPath basePath )
    {
        return ExportItemPath.from( basePath, SYSTEM_FOLDER_NAME );
    }

    public static ExportItemPath resolveOrderListPath( final ExportItemPath basePath )
    {
        return ExportItemPath.from( basePath, ORDER_EXPORT_NAME );
    }

    public static ExportItemPath resolveNodeXmlPath( ExportItemPath basePath )
    {
        return ExportItemPath.from( basePath, NODE_XML_EXPORT_NAME );
    }

}
