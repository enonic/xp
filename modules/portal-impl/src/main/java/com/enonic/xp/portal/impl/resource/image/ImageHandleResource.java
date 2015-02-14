package com.enonic.xp.portal.impl.resource.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.core.image.BuilderContext;
import com.enonic.xp.core.image.ImageFilter;
import com.enonic.xp.core.image.ImageHelper;
import com.enonic.xp.portal.impl.resource.base.BaseResource;

public final class ImageHandleResource
    extends BaseResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    @QueryParam("filter")
    protected String filterParam;

    @QueryParam("quality")
    protected int quality = DEFAULT_QUALITY;

    @QueryParam("background")
    protected String background;

    protected ByteSource binary;

    protected String mimeType;

    protected String name;

    @GET
    public Response handle()
        throws Exception
    {
        final BufferedImage contentImage = toBufferedImage( this.binary );
        final String format = getFormat( this.name );
        final BufferedImage image = applyFilters( contentImage, format );

        final byte[] imageData = serializeImage( image, format );
        return Response.ok().type( this.mimeType ).entity( imageData ).build();
    }

    private BufferedImage applyFilters( final BufferedImage sourceImage, final String format )
    {
        if ( Strings.isNullOrEmpty( this.filterParam ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.services.getImageFilterBuilder().build( new BuilderContext(), this.filterParam );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, getBackgroundColor() );
        }
        else
        {
            return targetImage;
        }
    }

    private BufferedImage toBufferedImage( final ByteSource byteSource )
        throws Exception
    {
        try (final InputStream inputStream = byteSource.openStream())
        {
            return ImageIO.read( inputStream );
        }
    }

    private byte[] serializeImage( final BufferedImage image, final String format )
        throws Exception
    {
        return ImageHelper.writeImage( image, format, getImageQuality() );
    }

    private String getFormat( final String fileName )
    {
        return StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
    }

    private int getImageQuality()
    {
        return ( this.quality > 0 ) && ( this.quality <= 100 ) ? this.quality : DEFAULT_QUALITY;
    }

    private int getBackgroundColor()
    {
        if ( Strings.isNullOrEmpty( this.background ) )
        {
            return DEFAULT_BACKGROUND;
        }

        String color = this.background;
        if ( color.startsWith( "0x" ) )
        {
            color = this.background.substring( 2 );
        }

        try
        {
            return Integer.parseInt( color, 16 );
        }
        catch ( final Exception e )
        {
            return DEFAULT_BACKGROUND;
        }
    }
}
