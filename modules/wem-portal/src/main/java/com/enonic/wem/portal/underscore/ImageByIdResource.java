package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;

import static com.enonic.wem.api.command.Commands.attachment;
import static org.apache.commons.lang.StringUtils.substringAfterLast;

@Path("{mode}/{contextualContent:.+}/_/image/id/{id:.+}")
public final class ImageByIdResource
    extends AbstractImageResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("contextualContent")
    protected String contextualContentAsString;

    @PathParam("id")
    protected String imageContentIdAsString;

    @QueryParam("filter")
    protected String filter;

    @QueryParam("background")
    protected String backgroundColor;

    @QueryParam("quality")
    protected String quality;

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;

    @Override
    String getFilterParam()
    {
        return this.filter;
    }

    @Override
    String getQualityParam()
    {
        return quality;
    }

    @Override
    String getBackgroundColorParam()
    {
        return backgroundColor;
    }

    @GET
    public Response getResource()
        throws IOException
    {
        final ContentId imageContentId = ContentId.from( imageContentIdAsString );
        final Content imageContent = getContent( imageContentId );

        final Attachment attachment = getAttachment( imageContent.getId() );
        if ( attachment == null )
        {
            throw new RuntimeException( "Attachment not found: " + imageContent.getName().toString() );
        }

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw new RuntimeException( "Blob not found: " + attachment.getBlobKey() );
        }
        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        final String format = resolveFormat( attachment );

        final BufferedImage image = applyFilters( contentImage, format );

        byte[] imageData = ImageHelper.writeImage( image, format, resolveQuality() );

        return Response.ok( imageData, attachment.getMimeType() ).build();
    }

    Attachment getAttachment( final ContentId contentId )
    {
        // TODO : Better not found handling
        try
        {
            return client.execute( attachment().getAll().contentId( contentId ) ).first();
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private String resolveFormat( final Attachment attachment )
    {
        return substringAfterLast( attachment.getName(), "." );
    }
}
