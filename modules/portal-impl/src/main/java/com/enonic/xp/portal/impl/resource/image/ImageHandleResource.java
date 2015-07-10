package com.enonic.xp.portal.impl.resource.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.scale.ScaleParams;
import com.enonic.xp.portal.impl.resource.base.BaseResource;
import com.enonic.xp.portal.impl.util.FilesHelper;

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

    protected String path;

    protected ByteSource binary;

    protected String mimeType;

    protected String name;

    protected ScaleParams scaleParams;

    protected FocalPoint focalPoint = FocalPoint.DEFAULT;

    protected Cropping cropping;

    @GET
    public Response handle()
        throws Exception
    {
        final Path cachedImagePath = getCachedImagePath();
        byte[] imageData = FilesHelper.readAllBytes( cachedImagePath );
        if ( imageData == null )
        {
            final BufferedImage contentImage = toBufferedImage( this.binary );
            final String format = getFormat( this.name );
            BufferedImage image = applyCropping( contentImage );
            image = applyScaling( image );
            image = applyFilters( image, format );

            imageData = serializeImage( image, format );
            FilesHelper.write( cachedImagePath, imageData );
        }
        return Response.ok().type( this.mimeType ).entity( imageData ).build();
    }

    private BufferedImage applyFilters( final BufferedImage sourceImage, final String format )
    {
        if ( Strings.isNullOrEmpty( this.filterParam ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.services.getImageFilterBuilder().build( this.filterParam );
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

    private BufferedImage applyScaling( final BufferedImage sourceImage )
    {
        if ( this.scaleParams == null )
        {
            return sourceImage;
        }

        final ImageScaleFunction imageScaleFunction =
            this.services.getImageScaleFunctionBuilder().build( this.scaleParams, this.focalPoint );
        final BufferedImage targetImage = imageScaleFunction.scale( sourceImage );

        return targetImage;
    }

    private BufferedImage applyCropping( final BufferedImage sourceImage )
    {
        if ( this.cropping == null )
        {
            return sourceImage;
        }
        return sourceImage.getSubimage( cropping.left(), cropping.top(), cropping.width(), cropping.height() );
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

    private Path getCachedImagePath()
    {
        final String homeDir = HomeDir.get().toString();
        final String filter = this.filterParam != null ? this.filterParam : "no-filter";
        final String quality = Integer.toString( this.quality );
        final String background = this.background != null ? this.background : "no-background";
        final String path = this.path;

        return Paths.get( homeDir, "work", "cache", "img", filter, quality, background, path ).
            toAbsolutePath();
    }
}
