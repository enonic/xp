package com.enonic.wem.portal.internal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.ImageMediaHelper;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.internal.base.BaseResource;

@Path("/portal/{mode}/{workspace}/{contentPath:.+}/_/image")
public final class ImageResource
    extends BaseResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    protected ImageFilterBuilder imageFilterBuilder;

    protected BlobService blobService;

    protected ContentService contentService;

    protected Workspace workspace;

    protected ContentPath contentPath;

    @QueryParam("filter")
    protected String filterParam;

    protected int quality = DEFAULT_QUALITY;

    protected int backgroundColor = DEFAULT_BACKGROUND;

    @PathParam("workspace")
    public void setWorkspace( final String value )
    {
        this.workspace = Workspace.from( value );
    }

    @QueryParam("quality")
    public void setQuality( final String value )
    {
        this.quality = parseQuality( value );
    }

    @QueryParam("background")
    public void setBackground( final String value )
    {
        this.backgroundColor = parseBackgroundColor( value );
    }

    @PathParam("contentPath")
    public void setContentPath( final String value )
    {
        this.contentPath = ContentPath.from( value );
    }

    private BufferedImage toBufferedImage( final InputStream dataStream )
    {
        try
        {
            return ImageIO.read( dataStream );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private byte[] serializeImage( final BufferedImage image, final String format )
    {
        try
        {
            return ImageHelper.writeImage( image, format, this.quality );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private static int parseQuality( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return DEFAULT_QUALITY;
        }

        final Integer num = Ints.tryParse( value );
        return ( num >= 0 ) && ( num <= 100 ) ? num : DEFAULT_QUALITY;
    }

    private static int parseBackgroundColor( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return DEFAULT_BACKGROUND;
        }

        String color = value;
        if ( color.startsWith( "0x" ) )
        {
            color = value.substring( 2 );
        }

        try
        {
            return Integer.parseInt( color, 16 );
        }
        catch ( Exception e )
        {
            return DEFAULT_BACKGROUND;
        }
    }

    private String getFormat( final String fileName )
    {
        return StringUtils.substringAfterLast( fileName, "." ).toLowerCase();
    }

    private BufferedImage applyFilters( final BufferedImage sourceImage, final String format )
    {
        if ( Strings.isNullOrEmpty( this.filterParam ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), this.filterParam );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, this.backgroundColor );
        }
        else
        {
            return targetImage;
        }
    }

    private Blob getBlob( final BlobKey blobKey )
    {
        return this.blobService.get( blobKey );
    }

    private Content getContent( final ContentId contentId )
    {
        final Content content = this.contentService.getById( contentId );
        if ( content != null )
        {
            return content;
        }

        throw notFound( "Content with id [%s] not found", contentId.toString() );
    }

    private Content getContent( final ContentPath contentPath )
    {
        final Content content = this.contentService.getByPath( contentPath );
        if ( content != null )
        {
            return content;
        }

        throw notFound( "Content with path [%s] not found", contentPath.toString() );
    }

    private Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        try
        {
            final Content content = contentService.getById( contentId );
            return content.getAttachments().getAttachment( attachmentName );
        }
        catch ( ContentNotFoundException e )
        {
            throw notFound( "Attachment [%s] for content [%s] not found", attachmentName, contentId.toString() );
        }
    }

    @GET
    @Path("id/{imageId}")
    public Response getById( @PathParam("imageId") final String id )
    {
        final ContentId imageContentId = ContentId.from( id );
        final Content imageContent = getContent( imageContentId );

        final Attachment attachment = ImageMediaHelper.getImageAttachment( imageContent );
        if ( attachment == null )
        {
            throw notFound( "Attachment [%s] not found", imageContent.getName().toString() );
        }

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw notFound( "Blob [%s] not found", attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );
        final String format = getFormat( attachment.getName() );
        final BufferedImage image = applyFilters( contentImage, format );

        final byte[] imageData = serializeImage( image, format );
        return Response.ok().type( attachment.getMimeType() ).entity( imageData ).build();
    }

    @GET
    @Path("{fileName}")
    public Response getByName( @PathParam("fileName") final String fileName )
    {
        final Content content = getContent( this.contentPath );
        final Attachment attachment = getAttachment( content.getId(), fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw notFound( "Blob [%s] not found", attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );
        final String format = getFormat( fileName );
        final BufferedImage image = applyFilters( contentImage, format );

        final byte[] imageData = serializeImage( image, format );
        return Response.ok().type( attachment.getMimeType() ).entity( imageData ).build();
    }
}
