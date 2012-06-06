package com.enonic.wem.web.rest.account;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.core.security.user.UserEntity;

@Service
public final class UserPhotoService
{

    public UserPhotoService()
    {
    }

    public BufferedImage renderPhoto( final UserEntity user, final int size )
        throws Exception
    {
        final BufferedImage image = createImage( user.getPhoto() );
        return resizeImage( image, size );

    }

    private BufferedImage toBufferedImage( final byte[] data )
        throws Exception
    {
        return ImageIO.read( new ByteArrayInputStream( data ) );
    }

    private BufferedImage createImage( final byte[] data )
        throws Exception
    {
        return toBufferedImage( data );
    }

    private BufferedImage resizeImage( final BufferedImage image, final int size )
        throws Exception
    {
        return new ScaleSquareFilter( size ).filter( image );
    }

}
