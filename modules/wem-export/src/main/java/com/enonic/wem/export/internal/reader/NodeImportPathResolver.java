package com.enonic.wem.export.internal.reader;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Strings;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;

public class NodeImportPathResolver
{
    public static NodePath resolveImportedNodePath( final Path filePath, final Path exportRootPath, final NodePath importRoot )
    {
        final Path relativeFilePath = exportRootPath.relativize( filePath );

        final NodePath.Builder builder = NodePath.newPath( importRoot );

        relativeFilePath.forEach( ( path ) -> {
            builder.addElement( path.toString() );
        } );

        return builder.build();
    }

    public static Path resolveOrderFilePath( final Path nodeBasePath )
    {
        return Paths.get( nodeBasePath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME, NodeExportPathResolver.ORDER_EXPORT_NAME );
    }

    public static Path resolveBinaryFilePath( final Path nodeBasePath, final String binaryReference )
    {
        return Paths.get( nodeBasePath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME, NodeExportPathResolver.BINARY_FOLDER,
                          binaryReference );
    }


    public static Path resolveNodeXmlFilePath( final Path nodeBasePath )
    {
        return Paths.get( nodeBasePath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME, NodeExportPathResolver.NODE_XML_EXPORT_NAME );
    }

    public static Path resolveChildNodePath( final Path parentPath, final String childName )
    {
        if ( Strings.isNullOrEmpty( childName ) )
        {
            return null;
        }

        return Paths.get( parentPath.toString(), childName );
    }

}
