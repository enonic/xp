package com.enonic.wem.export.internal.reader;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.vfs.VirtualFile;

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
        final Path binaryPath = Paths.get( SYSTEM_FOLDER_NAME, BINARY_FOLDER, binaryReferenceString );

        final VirtualFile binaryFile = nodeFolder.resolve( binaryPath.toString() );

        if ( !binaryFile.exists() )
        {
            throw new ImportNodeException( "Missing binary source, expected at: " + binaryFile.getPath() );
        }

        return binaryFile;
    }

    public VirtualFile getOrderSource( final VirtualFile nodeFolder )
    {
        final Path orderPath = Paths.get( SYSTEM_FOLDER_NAME, ORDER_EXPORT_NAME );

        final VirtualFile orderFile = nodeFolder.resolve( orderPath.toString() );

        if ( !orderFile.exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected at:" + orderFile.getPath() );
        }

        return orderFile;
    }


    public VirtualFile getNodeSource( final VirtualFile nodeFolder )
    {
        final Path nodePath = Paths.get( SYSTEM_FOLDER_NAME, NODE_XML_EXPORT_NAME );

        final VirtualFile nodeVF = nodeFolder.resolve( nodePath.toString() );

        if ( !nodeVF.exists() )
        {
            throw new ImportNodeException( "Missing node source, expected at: " + nodeVF.getPath() );
        }

        return nodeVF;
    }


}
