package com.enonic.xp.portal.impl.resource.image;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ReadImageParams;
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

    protected String id;

    protected String binaryRef;

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
            final String format = getFormat( this.name );
            final ReadImageParams imageParams = ReadImageParams.newImageParams().
                cropping( cropping ).
                scaleParams( scaleParams ).
                focalPoint( focalPoint ).
                filterParam( filterParam ).
                backgroundColor( getBackgroundColor() ).
                format( format ).
                build();
            final BufferedImage bufferedImage = services.getImageService().readImage( this.binary, imageParams );

            imageData = serializeImage( bufferedImage, format );
            FilesHelper.write( cachedImagePath, imageData );
        }

        return Response.ok().type( this.mimeType ).entity( imageData ).build();
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
        final String scale = this.scaleParams != null ? this.scaleParams.toString() : "no-scale";
        final String focal = this.focalPoint != null ? this.focalPoint.toString() : "no-focal";
        final String cropping = this.cropping != null ? this.cropping.toString() : "no-cropping";
        final String filter = this.filterParam != null ? this.filterParam : "no-filter";
        final String quality = Integer.toString( this.quality );
        final String background = this.background != null ? this.background : "no-background";

        return Paths.get( homeDir, "work", "cache", "img", this.id, this.binaryRef, scale, focal, cropping, filter, quality, background,
                          name ).
            toAbsolutePath();
    }
}
