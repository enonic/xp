package com.enonic.wem.web.rest.resource.account;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.elasticsearch.common.collect.Maps;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;

import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;

final class AccountImageHelper
{
    private final Map<String, BufferedImage> cache;

    public AccountImageHelper()
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

    public BufferedImage getDefaultImage( final String key, final int size )
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

    public BufferedImage getAccountImage( final Account account, final int size )
        throws Exception
    {
        if ( account == null )
        {
            return null;
        }

        if ( !( account instanceof UserAccount ) )
        {
            return null;
        }

        final UserAccount user = (UserAccount) account;
        if ( user.getImage() == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( user.getImage() );
        return resizeImage( image, size );
    }

    private BufferedImage toBufferedImage( final byte[] data )
        throws Exception
    {
        return ImageIO.read( new ByteArrayInputStream( data ) );
    }
}
