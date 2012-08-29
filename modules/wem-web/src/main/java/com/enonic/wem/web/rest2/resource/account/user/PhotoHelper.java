package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.core.security.user.UserEntity;

final class PhotoHelper
{
    public static BufferedImage renderPhoto( final UserEntity user, final int size )
        throws Exception
    {
        if (user.getPhoto() == null) {
            return null;
        }

        final BufferedImage image = toBufferedImage( user.getPhoto() );
        return resizeImage( image, size );
    }

    private static BufferedImage toBufferedImage( final byte[] data )
        throws Exception
    {
        return ImageIO.read( new ByteArrayInputStream( data ) );
    }

    private static BufferedImage resizeImage( final BufferedImage image, final int size )
        throws Exception
    {
        return new ScaleSquareFilter( size ).filter( image );
    }
}
