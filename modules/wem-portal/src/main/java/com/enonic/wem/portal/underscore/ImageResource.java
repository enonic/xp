package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.base.Throwables;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.attachment;
import static com.enonic.wem.api.command.Commands.blob;
import static com.enonic.wem.api.command.Commands.content;

@Path("{mode}/{path:.+}/_/image/{fileName:.+}")
public final class ImageResource
    extends UnderscoreResource
{

    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("fileName")
    protected String fileName;

    @GET
    public Response getResource()
    {
        final ContentPath path = ContentPath.from( contentPath );
        final Content content = getContent( path );

        final Attachment attachment = getAttachment( content.getId(), fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        return Response.ok( contentImage, attachment.getMimeType() ).build();
    }

    private Content getContent( final ContentPath contentPath )
    {
        final Content content = client.execute( content().get().byPath( contentPath ) );
        if ( content != null )
        {
            return content;
        }

        throw PortalWebException.notFound().message( "Content with path [{0}] not found.", contentPath ).build();
    }

    private Attachment getAttachment( final ContentId contentId, final String attachmentName )
    {
        final Attachment attachment = client.execute( attachment().get().contentId( contentId ).attachmentName( attachmentName ) );
        if ( attachment != null )
        {
            return attachment;
        }

        throw PortalWebException.notFound().message( "Image [{0}] not found for path [{1}].", fileName, contentPath ).build();
    }

    private Blob getBlob( final BlobKey blobKey )
    {
        final Blob blob = client.execute( blob().get( blobKey ) );
        if ( blob != null )
        {
            return blob;
        }

        throw PortalWebException.notFound().message( "Image [{0}] not found for path [{1}].", fileName, contentPath ).build();
    }

    private BufferedImage toBufferedImage( final InputStream dataStream )
    {
        try
        {
            return ImageIO.read( dataStream );
        }
        catch ( IOException e )
        {
            throw Throwables.propagate( e );
        }
    }
}
