package com.enonic.wem.export.internal.reader;

import java.net.URL;
import java.util.stream.Stream;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.api.vfs.VirtualFilePath;

import static com.enonic.wem.export.internal.ExportConstants.BINARY_FOLDER;
import static com.enonic.wem.export.internal.ExportConstants.NODE_XML_EXPORT_NAME;
import static com.enonic.wem.export.internal.ExportConstants.ORDER_EXPORT_NAME;
import static com.enonic.wem.export.internal.ExportConstants.SYSTEM_FOLDER_NAME;

public class ExportReader
{
    public Stream<VirtualFile> getChildren( final VirtualFile parent )
    {
        return parent.getChildren().stream().
            filter( VirtualFile::isFolder ).
            filter( ( folder ) -> !getLastBitFromUrl( folder.getUrl() ).equals( SYSTEM_FOLDER_NAME ) );
    }

    private static String getLastBitFromUrl( final URL url )
    {
        return url.toString().replaceFirst( ".*/([^/?]+).*", "$1" );
    }

    public VirtualFile getBinarySource( final VirtualFile nodeFolder, final String binaryReferenceString )
    {
        final VirtualFilePath binaryFilePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, BINARY_FOLDER, binaryReferenceString );

        final VirtualFile binaryFile = nodeFolder.resolve( binaryFilePath.getPath() );

        if ( !binaryFile.exists() )
        {
            throw new ImportNodeException( "Missing binary source, expected at: " + binaryFile.getPath() );
        }

        return binaryFile;
    }

    public VirtualFile getOrderSource( final VirtualFile nodeFolder )
    {
        final VirtualFilePath orderSourcePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, ORDER_EXPORT_NAME );

        final VirtualFile orderFile = nodeFolder.resolve( orderSourcePath.getPath() );

        if ( !orderFile.exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected at:" + orderFile.getPath() );
        }

        return orderFile;
    }

    public VirtualFile getNodeSource( final VirtualFile nodeFolder )
    {
        final VirtualFilePath nodeSourcePath = nodeFolder.getPath().join( SYSTEM_FOLDER_NAME, NODE_XML_EXPORT_NAME );

        final VirtualFile nodeVF = nodeFolder.resolve( nodeSourcePath.getPath() );

        if ( !nodeVF.exists() )
        {
            throw new ImportNodeException( "Missing node source, expected at: " + nodeVF.getPath() );
        }

        return nodeVF;
    }


}
