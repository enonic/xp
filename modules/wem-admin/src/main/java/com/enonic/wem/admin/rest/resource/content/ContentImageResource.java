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
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.icon.Icon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleMax;
import static com.enonic.wem.admin.rest.resource.content.ContentImageHelper.ImageFilter.ScaleSquareFilter;
import static com.enonic.wem.api.command.Commands.contentType;


@Path("content/image")
@Produces("image/*")
public class ContentImageResource
{
    private ContentImageHelper helper;

    private Client client;

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
            // check if content has a thumbnail attachment ("_thumb.png")
            final Attachment attachmentThumbnail = findAttachment( contentId, CreateContent.THUMBNAIL_NAME );
            if ( attachmentThumbnail != null )
            {
                final Blob blob = client.execute( Commands.blob().get( attachmentThumbnail.getBlobKey() ) );
                final BufferedImage thumbnailImage = helper.getImageFromBlob( blob, size, ScaleSquareFilter );
                return Response.ok( thumbnailImage, attachmentThumbnail.getMimeType() ).build();
            }
        }

        final String mimeType;
        final BufferedImage contentImage;
        final ContentTypeName contentType = content.getType();
        if ( contentType.isImageMedia() )
        {
            final String attachmentName = getImageAttachmentName( content );
            final Attachment attachment = findAttachment( contentId, attachmentName );

            final Blob blob = client.execute( Commands.blob().get( attachment.getBlobKey() ) );
            if ( thumbnail )
            {
                contentImage = helper.getImageFromBlob( blob, size, ScaleSquareFilter );
            }
            else
            {
                contentImage = helper.getImageFromBlob( blob, size, ScaleMax );
            }
            mimeType = attachment.getMimeType();
        }
        else
        {
            final Icon contentTypeIcon = findRootContentTypeIcon( contentType );
            final Blob blob = client.execute( Commands.blob().get( contentTypeIcon.getBlobKey() ) );
            if ( blob == null )
            {
                throw new WebApplicationException( Response.Status.NOT_FOUND );
            }
            contentImage = helper.resizeImage( blob, size );
            mimeType = contentTypeIcon.getMimeType();
        }

        return Response.ok( contentImage, mimeType ).build();
    }

    private String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();

        final Property imageProperty = contentData.getProperty( "image" );
        return imageProperty == null ? content.getName() : imageProperty.getString();
    }

    private Icon findRootContentTypeIcon( final ContentTypeName contentTypeName )
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
        return client.execute( contentType().get().byNames().contentTypeNames( contentTypeNames ) ).first();
    }

    private Content findContent( final ContentId contentId )
    {
        return client.execute( Commands.content().get().byId( contentId ) );
    }

    private Attachment findAttachment( final ContentId contentId, final String attachmentName )
    {
        return client.execute( Commands.attachment().get().contentId( contentId ).attachmentName( attachmentName ) );
    }
}
