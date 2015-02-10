package com.enonic.wem.export.internal.reader;

import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Joiner;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.export.util.PathUtils;

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
        final VirtualFile binaryFile = createVFSPath( nodeFolder, SYSTEM_FOLDER_NAME, BINARY_FOLDER, binaryReferenceString );

        if ( !binaryFile.exists() )
        {
            throw new ImportNodeException( "Missing binary source, expected at: " + binaryFile.getPath() );
        }

        return binaryFile;
    }

    public VirtualFile getOrderSource( final VirtualFile nodeFolder )
    {
        final VirtualFile orderFile = createVFSPath( nodeFolder, SYSTEM_FOLDER_NAME, ORDER_EXPORT_NAME );

        if ( !orderFile.exists() )
        {
            throw new ImportNodeException( "Parent has manual ordering of children, expected at:" + orderFile.getPath() );
        }

        return orderFile;
    }

    public VirtualFile getNodeSource( final VirtualFile nodeFolder )
    {
        final VirtualFile nodeVF = createVFSPath( nodeFolder, SYSTEM_FOLDER_NAME, NODE_XML_EXPORT_NAME );

        if ( !nodeVF.exists() )
        {
            throw new ImportNodeException( "Missing node source, expected at: " + nodeVF.getPath() );
        }

        return nodeVF;
    }

    private VirtualFile createVFSPath( final VirtualFile nodeFolder, final String... paths )
    {
        final List<String> pathElements = PathUtils.joinPaths( paths );

        return nodeFolder.resolve( Joiner.on( "/" ).join( pathElements ) );
    }
}
