package com.enonic.xp.portal.impl;

import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Media;

/**
 * Which images the image API scales. The others it serves as stored, whatever the scale, filter or format of the URL,
 * so they have no {@code srcset} of their own.
 */
public final class ImageScaling
{
    private static final MediaType SVG_MEDIA_TYPE = MediaType.SVG_UTF_8.withoutParameters();

    private ImageScaling()
    {
    }

    /**
     * @param mimeType type of the stored image
     * @return whether the image API scales an image of the type
     */
    public static boolean isScalable( final MediaType mimeType )
    {
        return !( mimeType.is( MediaType.GIF ) || mimeType.is( MediaType.AVIF ) || mimeType.is( MediaType.WEBP ) ||
            mimeType.is( SVG_MEDIA_TYPE ) );
    }

    /**
     * @return the type the attachment is served as
     */
    public static MediaType mimeType( final Attachment attachment )
    {
        if ( "svgz".equals( attachment.getExtension() ) )
        {
            return SVG_MEDIA_TYPE;
        }
        try
        {
            return MediaType.parse( attachment.getMimeType() );
        }
        catch ( IllegalArgumentException e )
        {
            return MediaType.OCTET_STREAM;
        }
    }

    /**
     * @return whether the image API scales the source of the image
     */
    public static boolean isScalable( final Media media )
    {
        final Attachment source = media.getAttachments().byLabel( "source" );
        return source == null || isScalable( mimeType( source ) );
    }
}
