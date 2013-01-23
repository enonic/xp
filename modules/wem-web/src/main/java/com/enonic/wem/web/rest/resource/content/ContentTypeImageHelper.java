package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

final class ContentTypeImageHelper
{

    public ContentTypeImageHelper()
        throws Exception
    {
    }

    public BufferedImage getContentTypeIcon( final ContentType contentType, final int size )
        throws Exception
    {
        if ( ( contentType == null ) || ( contentType.getIcon() == null ) || ( contentType.getIcon().length == 0 ) )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( contentType.getIcon() );
        return resizeImage( image, size );
    }

    private BufferedImage toBufferedImage( final byte[] data )
        throws Exception
    {
        return ImageIO.read( new ByteArrayInputStream( data ) );
    }

    private BufferedImage resizeImage( final BufferedImage image, final int size )
        throws Exception
    {
        return new ScaleSquareFilter( size ).filter( image );
    }
}
