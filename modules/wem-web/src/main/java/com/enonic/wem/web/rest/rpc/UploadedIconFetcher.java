package com.enonic.wem.web.rest.rpc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.Icon;
import com.enonic.wem.core.image.filter.effect.ScaleHeightFilter;
import com.enonic.wem.core.image.filter.effect.ScaleWidthFilter;
import com.enonic.wem.web.json.rpc.JsonRpcError;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

public final class UploadedIconFetcher
{
    private static final int MAX_ICON_SIZE = 512;

    private final static Set<String> VALID_ICON_MIME_TYPES =
        ImmutableSet.of( "image/gif", "image/jpeg", "image/png", "image/tiff", "image/bmp" );

    private final UploadService uploadService;

    public UploadedIconFetcher( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }

    public Icon getUploadedIcon( final String iconReference )
        throws IOException, JsonRpcException
    {
        if ( iconReference == null )
        {
            return null;
        }
        final UploadItem uploadItem = uploadService.getItem( iconReference );
        if ( uploadItem == null )
        {
            return null;
        }

        final String mimeType = uploadItem.getMimeType();
        if ( !isValidIconMimeType( mimeType ) )
        {
            throw new JsonRpcException( JsonRpcError.invalidParams( String.format( "Unsupported image type: %s", mimeType ) ) );
        }

        final byte[] iconData = getUploadedImageData( uploadItem );
        final BufferedImage image = ImageIO.read( new ByteArrayInputStream( iconData ) );
        if ( image == null )
        {
            throw new JsonRpcException( JsonRpcError.invalidParams( "Unable to read image file" ) );
        }

        final Icon icon = Icon.from( iconData, mimeType );
        return adjustIconSize( icon, image, MAX_ICON_SIZE );
    }

    private Icon adjustIconSize( final Icon icon, final BufferedImage image, final int maxSize )
        throws IOException
    {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        if ( ( imageWidth > maxSize ) || ( imageHeight > maxSize ) )
        {
            if ( imageWidth > imageHeight )
            {
                final BufferedImage resizedImage = new ScaleWidthFilter( maxSize ).filter( image );
                return imageToIcon( resizedImage, icon.getMimeType() );
            }
            else
            {
                final BufferedImage resizedImage = new ScaleHeightFilter( maxSize ).filter( image );
                return imageToIcon( resizedImage, icon.getMimeType() );
            }
        }
        else
        {
            return icon;
        }
    }

    private byte[] getUploadedImageData( final UploadItem uploadItem )
        throws IOException
    {
        final File file = uploadItem.getFile();
        if ( file.exists() )
        {
            return FileUtils.readFileToByteArray( file );
        }
        return null;
    }

    private boolean isValidIconMimeType( final String mimeType )
    {
        return ( mimeType != null ) && VALID_ICON_MIME_TYPES.contains( mimeType.toLowerCase() );
    }

    private Icon imageToIcon( final BufferedImage image, final String mimeType )
        throws IOException
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write( image, getWriterFormatName( mimeType ), output );
        return Icon.from( output.toByteArray(), mimeType );
    }

    private String getWriterFormatName( final String mimeType )
    {
        final Iterator<ImageWriter> ite = ImageIO.getImageWritersByMIMEType( mimeType );
        if ( !ite.hasNext() )
        {
            return null;
        }
        return ite.next().getOriginatingProvider().getFormatNames()[0];
    }

}
