package com.enonic.xp.core.impl.export.reader;

import java.net.URL;
import java.util.stream.Stream;

import com.enonic.xp.export.ImportNodeException;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;

import static com.enonic.xp.core.impl.export.ExportConstants.BINARY_FOLDER;
import static com.enonic.xp.core.impl.export.ExportConstants.NODE_XML_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.ExportConstants.ORDER_EXPORT_NAME;
import static com.enonic.xp.core.impl.export.ExportConstants.SYSTEM_FOLDER_NAME;

public class ExportReader
{
    private static String getLastBitFromUrl( final URL url )
    {
        return url.toString().replaceFirst( ".*/([^/?]+).*", "$1" );
    }

    public long getNodeFileCount( final VirtualFile virtualFile )
    {
        long nodeFileCount = virtualFile.getChildren().
            stream().
            mapToLong( this::getNodeFileCount ).
            sum();
        if ( NODE_XML_EXPORT_NAME.equals( virtualFile.getName() ) )
        {
            nodeFileCount++;
        }
        return nodeFileCount;
    }

    public Stream<VirtualFile> getChildren( final VirtualFile parent )
    {
        return parent.getChildren().stream().
            filter( VirtualFile::isFolder ).
            filter( ( folder ) -> !getLastBitFromUrl( folder.getUrl() ).equals( SYSTEM_FOLDER_NAME ) );
    }

    public VirtualFile getBinarySource( final VirtualFile nodeFolder, final String binaryReferenceString )
    {
        final VirtualFilePath binaryFilePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, BINARY_FOLDER, binaryReferenceString );

        final VirtualFile binaryFile = nodeFolder.resolve( binaryFilePath );

        if ( !binaryFile.exists() )
        {
            throw new ImportNodeException( "Missing binary source, expected at: " + binaryFile.getPath() );
        }

        return binaryFile;
    }

    public VirtualFile getOrderSource( final VirtualFile nodeFolder )
    {
        final VirtualFilePath orderSourcePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, ORDER_EXPORT_NAME );

        final VirtualFile orderFile = nodeFolder.resolve( orderSourcePath );

        if ( !orderFile.exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected at:" + orderFile.getPath() );
        }

        return orderFile;
    }

    public VirtualFile getNodeSource( final VirtualFile nodeFolder )
    {
        final VirtualFilePath nodeSourcePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, NODE_XML_EXPORT_NAME );

        final VirtualFile nodeVF = nodeFolder.resolve( nodeSourcePath );

        if ( !nodeVF.exists() )
        {
            throw new ImportNodeException( "Missing node source, expected at: " + nodeVF.getPath() );
        }

        return nodeVF;
    }


}
