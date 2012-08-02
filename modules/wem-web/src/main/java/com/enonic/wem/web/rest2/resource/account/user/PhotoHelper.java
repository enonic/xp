package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.core.security.user.UserEntity;

final class PhotoHelper
{
    private final BufferedImage defaultImage;

    public PhotoHelper()
        throws Exception
    {
        this.defaultImage = ImageIO.read( getClass().getResource( "x-user-photo.png" ) );
    }

    public BufferedImage renderPhoto( final UserEntity user, final int size )
        throws Exception
    {
        BufferedImage image = this.defaultImage;

        if (user.getPhoto() != null) {
            image = toBufferedImage( user.getPhoto() );
        }

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
