package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.enonic.wem.api.Icon;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

final class ContentTypeImageHelper
{

    public ContentTypeImageHelper()
    {
    }

    public BufferedImage getContentTypeIcon( final Icon icon, final int size )
        throws Exception
    {
        if ( icon == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( icon.getData() );
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
