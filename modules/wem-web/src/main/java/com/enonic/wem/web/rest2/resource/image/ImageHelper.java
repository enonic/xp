package com.enonic.wem.web.rest2.resource.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.elasticsearch.common.collect.Maps;

import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;

final class ImageHelper
{
    private final Map<String, BufferedImage> cache;

    public ImageHelper()
        throws Exception
    {
        this.cache = Maps.newHashMap();
        populate( "admin" );
        populate( "anonymous" );
        populate( "user" );
        populate( "role" );
        populate( "group" );
    }

    private void populate( final String name )
        throws Exception
    {
        this.cache.put( name, loadImage( name + ".png" ) );
    }

    private BufferedImage loadImage( final String name )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( name );
        if ( in == null )
        {
            throw new IOException( "Image [" + name + "] not found" );
        }

        return ImageIO.read( in );
    }

    public BufferedImage getImage( final String key, final int size )
        throws Exception
    {
        final BufferedImage image = this.cache.get( key );
        if ( image == null )
        {
            return null;
        }

        return resizeImage( image, size );
    }

    private BufferedImage resizeImage( final BufferedImage image, final int size )
        throws Exception
    {
        return new ScaleSquareFilter( size ).filter( image );
    }
}
