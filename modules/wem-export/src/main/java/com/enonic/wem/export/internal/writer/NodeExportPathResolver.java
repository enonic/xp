package com.enonic.wem.export.internal.writer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

public class NodeExportPathResolver
{

    private static final String SYSTEM_FOLDER_NAME = "_";

    public static ExportItemPath resolveRoot( final ExportItemPath basePath, final String exportName )
    {
        final String timestamp = LocalDateTime.now().format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );

        return ExportItemPath.from( basePath, exportName + "_" + timestamp );
    }

    public static ExportItemPath resolveNodeBasePath( final ExportItemPath rootPath, final Node node )
    {
        final NodePath nodePath = node.path();

        return ExportItemPath.from( rootPath, nodePath.asRelative().toString() );
    }

    public static ExportItemPath resolveSystemFolder( final ExportItemPath basePath )
    {
        return ExportItemPath.from( basePath, SYSTEM_FOLDER_NAME );
    }

    public static ExportItemPath resolveNodeXmlPath( ExportItemPath basePath, final Node node )
    {
        return ExportItemPath.from( basePath, node.name() + ".xml" );
    }

}
