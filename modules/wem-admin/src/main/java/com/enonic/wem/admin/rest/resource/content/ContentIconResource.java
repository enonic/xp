package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.GetAttachmentParameters;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.Property;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;
import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleSquareFilter;


@Path("content/icon")
@Produces("image/*")
public class ContentIconResource
{
    private static final ContentImageHelper helper = new ContentImageHelper();

    private AttachmentService attachmentService;

    private BlobService blobService;

    private ContentService contentService;

    @GET
    @Path("{contentId}")
    public Response getContentIcon( @PathParam("contentId") final String contentIdAsString,
                                    @QueryParam("size") @DefaultValue("128") final int size,
                                    @QueryParam("crop") @DefaultValue("true") final boolean crop, @QueryParam("ts") final String timestamp )
        throws Exception
    {

        if ( contentIdAsString == null )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content content = contentService.getById( contentId );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        ResolvedImage resolvedImage = resolveResponseFromContentThumbnail( content, size, crop );
        if ( resolvedImage.isOK() )
        {
            final boolean cacheForever = StringUtils.isNotEmpty( timestamp );
            if ( cacheForever )
            {
                final CacheControl cacheControl = new CacheControl();
                cacheControl.setMaxAge( Integer.MAX_VALUE );
                return resolvedImage.toResponse( cacheControl );
            }
            else
            {
                return resolvedImage.toResponse();
            }
        }
        else if ( content.getType().isImageMedia() )
        {
            resolvedImage = resolveResponseFromContentImageAttachment( content, size );
            if ( resolvedImage.isOK() )
            {
                return resolvedImage.toResponse();
            }
        }

        throw new WebApplicationException( Response.Status.NOT_FOUND );
    }

    private ResolvedImage resolveResponseFromContentThumbnail( final Content content, final int size, final boolean crop )
    {
        final Thumbnail contentThumbnail = content.getThumbnail();

        if ( contentThumbnail != null )
        {
            final Blob blob = blobService.get( contentThumbnail.getBlobKey() );
            if ( blob != null )
            {
                ImageFilter filter = crop ? ScaleSquareFilter : ScaleMax;
                final BufferedImage thumbnailImage = helper.getImageFromBlob( blob, size, filter );
                return new ResolvedImage( thumbnailImage, contentThumbnail.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    private ResolvedImage resolveResponseFromContentImageAttachment( final Content content, final int size )
    {
        final String attachmentName = getImageAttachmentName( content );
        final Attachment attachment = attachmentService.get( GetAttachmentParameters.create().
            contentId( content.getId() ).
            attachmentName( attachmentName ).
            build() );

        if ( attachment != null )
        {
            final Blob blob = blobService.get( attachment.getBlobKey() );
            if ( blob != null )
            {
                final BufferedImage contentImage = helper.getImageFromBlob( blob, size, ScaleSquareFilter );
                return new ResolvedImage( contentImage, attachment.getMimeType() );
            }
        }
        return ResolvedImage.unresolved();
    }

    private String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();

        final Property imageProperty = contentData.getProperty( "image" );
        return imageProperty == null ? content.getName().toString() : imageProperty.getString();
    }

    public void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }

    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
