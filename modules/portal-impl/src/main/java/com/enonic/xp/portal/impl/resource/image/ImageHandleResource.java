package com.enonic.xp.portal.impl.resource.image;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.scale.ScaleParams;
import com.enonic.xp.portal.impl.resource.base.BaseResource;
import com.enonic.xp.util.BinaryReference;

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

    protected ContentId contentId;

    protected BinaryReference binaryReference;

    protected String mimeType;

    protected String name;

    protected ScaleParams scaleParams;

    protected FocalPoint focalPoint = FocalPoint.DEFAULT;

    protected Cropping cropping;

    @GET
    public Response handle()
        throws Exception
    {
        final String format = getFormat( this.name );
        final ReadImageParams readImageParams = ReadImageParams.newImageParams().
            contentId( this.contentId ).
            binaryReference( this.binaryReference ).
            cropping( this.cropping ).
            scaleParams( this.scaleParams ).
            focalPoint( this.focalPoint ).
            filterParam( this.filterParam ).
            backgroundColor( getBackgroundColor() ).
            format( format ).
            quality( getImageQuality() ).
            build();

        final ByteSource imageData = this.services.getImageService().readImage( readImageParams );

        return Response.ok().type( this.mimeType ).entity( imageData.read() ).build();
    }

    private String getFormat( final String fileName )
        throws IOException
    {
        String format = StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
        if ( Strings.isNullOrEmpty( format ) )
        {
            format = this.services.getImageService().getFormatByMimeType( this.mimeType );
        }
        return format;
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
