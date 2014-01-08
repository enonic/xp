package com.enonic.wem.core.content;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.core.image.filter.effect.ScaleMaxFilter;

class ImageThumbnailResolver
{
    static final int THUMBNAIL_SIZE = 512;

    static InputSupplier<ByteArrayInputStream> resolve( final Blob originalImage )
    {
        try
        {
            final BufferedImage image = ImageIO.read( originalImage.getStream() );
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BufferedImage scaledImage = new ScaleMaxFilter( THUMBNAIL_SIZE ).filter( image );
            ImageIO.write( scaledImage, "png", outputStream );
            return ByteStreams.newInputStreamSupplier( outputStream.toByteArray() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to resolve image thumbnail: " + e.getMessage(), e );
        }
    }
}
