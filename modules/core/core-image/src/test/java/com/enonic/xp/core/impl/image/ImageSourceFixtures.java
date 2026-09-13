package com.enonic.xp.core.impl.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;

final class ImageSourceFixtures
{
    private ImageSourceFixtures() {}

    static byte[] avifWithContainerRotation()
    {
        // A 32x24 transparent raster with a red rectangle at (4,4)-(11,11), with explicit irot=3.
        // Encoded once so decoding tests do not depend on the encoder's container-orientation support.
        return Base64.getMimeDecoder().decode( """
            AAAAHGZ0eXBhdmlmAAAAAGF2aWZtaWYxbWlhZgAAAaNtZXRhAAAAAAAAACFoZGxyAAAAAAAAAABw
            aWN0AAAAAAAAAAAAAAAAAAAAAA5waXRtAAAAAAABAAAANGlsb2MAAAAAREAAAgACAAAAAAHHAAEA
            AAAAAAAAHQABAAAAAAHkAAEAAAAAAAAAJQAAADhpaW5mAAAAAAACAAAAFWluZmUCAAAAAAEAAGF2
            MDEAAAAAFWluZmUCAAAAAAIAAGF2MDEAAAAA4mlwcnAAAAC6aXBjbwAAABNjb2xybmNseAACAAIA
            BoAAAAAMYXYxQ4FAfAAAAAAUaXNwZQAAAAAAAAAgAAAAGAAAAA5waXhpAAAAAAEMAAAAOGF1eEMA
            AAAAdXJuOm1wZWc6bXBlZ0I6Y2ljcDpzeXN0ZW1zOmF1eGlsaWFyeTphbHBoYQAAAAAMYXYxQ4FA
            bAAAAAAUaXNwZQAAAAAAAAAgAAAAGAAAABBwaXhpAAAAAAMMDAwAAAAJaXJvdAMAAAAgaXBtYQAA
            AAAAAAACAAEFgYYHiIkAAgWCA4SFiQAAABppcmVmAAAAAAAAAA5hdXhsAAIAAQABAAAASm1kYXQS
            AAoGWBE/drqAMhFF/AAASAS8SxjTAbMSlnz0MBIACglYET92tAgIG4QyFkX8AAASAAA/ZvM8eA6c
            ok4mWQyPJKA=
            """ );
    }

    static byte[] jpegWithOrientation( final BufferedImage image, final int orientation ) throws IOException
    {
        final byte[] exif = ByteBuffer.allocate( 32 ).order( ByteOrder.LITTLE_ENDIAN )
            .put( "Exif\0\0II".getBytes( StandardCharsets.US_ASCII ) ).putShort( (short) 42 ).putInt( 8 )
            .putShort( (short) 1 ).putShort( (short) 0x112 ).putShort( (short) 3 ).putInt( 1 )
            .putShort( (short) orientation ).putShort( (short) 0 ).putInt( 0 ).array();
        return jpegWithMarker( image, 0xe1, exif );
    }

    static byte[] jpegWithLinearProfile() throws IOException
    {
        final var image = new BufferedImage( 8, 8, BufferedImage.TYPE_INT_RGB );
        for ( int y = 0; y < 8; y++ ) for ( int x = 0; x < 8; x++ ) image.setRGB( x, y, 0xff808080 );
        final var profile = new ByteArrayOutputStream();
        profile.write( "ICC_PROFILE\0".getBytes( StandardCharsets.US_ASCII ) );
        profile.write( 1 );
        profile.write( 1 );
        profile.write( ICC_Profile.getInstance( ColorSpace.CS_LINEAR_RGB ).getData() );
        return jpegWithMarker( image, 0xe2, profile.toByteArray() );
    }

    private static byte[] jpegWithMarker( final BufferedImage image, final int marker, final byte[] payload ) throws IOException
    {
        final var encoded = new ByteArrayOutputStream();
        ImageIO.write( image, "jpeg", encoded );
        final byte[] jpeg = encoded.toByteArray();
        final var result = new ByteArrayOutputStream();
        result.write( jpeg, 0, 2 );
        result.write( 0xff );
        result.write( marker );
        result.write( ( payload.length + 2 ) >>> 8 );
        result.write( ( payload.length + 2 ) & 255 );
        result.write( payload );
        result.write( jpeg, 2, jpeg.length - 2 );
        return result.toByteArray();
    }
}
