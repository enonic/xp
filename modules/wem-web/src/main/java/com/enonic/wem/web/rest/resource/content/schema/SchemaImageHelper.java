package com.enonic.wem.web.rest.resource.content.schema;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.enonic.wem.api.Icon;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

final class SchemaImageHelper
{
    private final BufferedImage defaultMixinImage;

    private final BufferedImage defaultRelationshipTypeImage;

    public SchemaImageHelper()
        throws Exception
    {
        defaultMixinImage = loadDefaultImage( "mixin" );
        defaultRelationshipTypeImage = loadDefaultImage( "relationshiptype" );
    }

    public BufferedImage getIconImage( final Icon icon, final int size )
        throws Exception
    {
        if ( icon == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( icon.getData() );
        return resizeImage( image, size );
    }

    public BufferedImage getDefaultMixinImage( final int size )
        throws Exception
    {
        return resizeImage( defaultMixinImage, size );
    }

    public BufferedImage getDefaultRelationshipTypeImage( final int size )
        throws Exception
    {
        return resizeImage( defaultRelationshipTypeImage, size );
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

    private BufferedImage loadDefaultImage( final String imageName )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( imageName + ".png" );
        if ( in == null )
        {
            throw new IOException( "Image [" + imageName + "] not found" );
        }

        return ImageIO.read( in );
    }

}
