package com.enonic.wem.admin.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.GetAttachmentParams;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.thumb.Thumbnail;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;
import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleSquareFilter;


@Path("content/image")
@Produces("image/*")
public class ContentImageResource
{
    private ContentImageHelper helper;

    @Inject
    private AttachmentService attachmentService;

    private Client client;

    @Inject
    private ContentTypeService contentTypeService;

    @Inject
    private BlobService blobService;

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
        this.helper = new ContentImageHelper( client );
    }

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentIdAsString,
                                     @QueryParam("size") @DefaultValue("128") final int size,
                                     @QueryParam("thumbnail") @DefaultValue("true") final boolean thumbnail )
        throws Exception
    {
        if ( contentIdAsString == null )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentId = ContentId.from( contentIdAsString );
        final Content content = findContent( contentId );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        if ( thumbnail )
        {
            final Thumbnail contentThumbnail = content.getThumbnail();

            if ( contentThumbnail != null )
            {
                final Blob blob = blobService.get( contentThumbnail.getBlobKey() );
                if ( blob != null )
                {
                    final BufferedImage thumbnailImage = helper.getImageFromBlob( blob, size, ScaleSquareFilter );
                    return Response.ok( thumbnailImage, contentThumbnail.getMimeType() ).build();
                }
            }
        }

        final String mimeType;
        final BufferedImage contentImage;
        final ContentTypeName contentType = content.getType();
        if ( contentType.isImageMedia() )
        {
            final String attachmentName = getImageAttachmentName( content );
            final Attachment attachment = findAttachment( contentId, attachmentName );
            if ( attachment != null )
            {
                final Blob blob = blobService.get( attachment.getBlobKey() );
                if ( blob != null )
                {
                    if ( thumbnail )
                    {
                        contentImage = helper.getImageFromBlob( blob, size, ScaleSquareFilter );
                    }
                    else
                    {
                        contentImage = helper.getImageFromBlob( blob, size, ScaleMax );
                    }
                    mimeType = attachment.getMimeType();
                    return Response.ok( contentImage, mimeType ).build();
                }
            }
        }

        final SchemaIcon contentTypeIcon = findRootContentTypeIcon( contentType );
        if ( contentTypeIcon == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        contentImage = helper.resizeImage( contentTypeIcon.asInputStream(), size );
        mimeType = contentTypeIcon.getMimeType();

        return Response.ok( contentImage, mimeType ).build();
    }

    private String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();

        final Property imageProperty = contentData.getProperty( "image" );
        return imageProperty == null ? content.getName().toString() : imageProperty.getString();
    }

    private SchemaIcon findRootContentTypeIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType == null ? null : contentType.getIcon();
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        final ContentTypeNames contentTypeNames = ContentTypeNames.from( contentTypeName );
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( contentTypeNames );

        return contentTypeService.getByNames( params ).first();
    }

    private Content findContent( final ContentId contentId )
    {
        return client.execute( Commands.content().get().byId( contentId ) );
    }

    private Attachment findAttachment( final ContentId contentId, final String attachmentName )
    {
        final GetAttachmentParams params = new GetAttachmentParams().contentId( contentId ).attachmentName( attachmentName );
        return attachmentService.get( params );
    }
}
