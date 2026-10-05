package com.enonic.xp.portal.impl;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageScalingTest
{
    @Test
    void rasterImages()
    {
        assertTrue( ImageScaling.isScalable( MediaType.PNG ) );
        assertTrue( ImageScaling.isScalable( MediaType.JPEG ) );
    }

    @Test
    void imagesServedAsStored()
    {
        for ( final MediaType type : new MediaType[]{MediaType.GIF, MediaType.WEBP, MediaType.AVIF, MediaType.SVG_UTF_8} )
        {
            assertFalse( ImageScaling.isScalable( type ), type.toString() );
        }
    }

    @Test
    void compressedSvgIsServedAsSvg()
    {
        final Attachment svgz = Attachment.create().name( "logo.svgz" ).mimeType( "application/octet-stream" ).label( "source" ).build();
        assertEquals( MediaType.SVG_UTF_8.withoutParameters(), ImageScaling.mimeType( svgz ) );
    }
}
