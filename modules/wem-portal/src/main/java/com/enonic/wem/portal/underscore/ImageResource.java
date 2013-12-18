package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.exception.PortalWebException;

import static com.enonic.wem.api.command.Commands.attachment;
import static com.enonic.wem.api.command.Commands.blob;
import static com.enonic.wem.api.command.Commands.content;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.substringAfterLast;

@Path("{mode}/{path:.+}/_/image/{fileName:.+}")
public final class ImageResource
    extends UnderscoreResource
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static int DEFAULT_QUALITY = 85;

    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("fileName")
    protected String fileName;

    @QueryParam("filter")
    protected String filter;

    @QueryParam("background")
    protected String backgroundColor;

    @QueryParam("quality")
    protected String quality;

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;

    @GET
    public Response getResource()
        throws IOException
    {
        final ContentPath path = ContentPath.from( contentPath );
        final Content content = getContent( path );

        final Attachment attachment = getAttachment( content.getId(), fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        final BufferedImage image = applyFilters( contentImage );

        byte[] imageData = ImageHelper.writeImage( image, getFormat(), getQuality() );

        return Response.ok( imageData, attachment.getMimeType() ).build();
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

    private BufferedImage applyFilters( final BufferedImage sourceImage )
    {
        if ( isNullOrEmpty( filter ) )
        {
            return sourceImage;
        }

        final ImageFilter imageFilter = this.imageFilterBuilder.build( new BuilderContext(), filter );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( getFormat() ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, getBackgroundColor() );
        }
        else
        {
            return targetImage;
        }
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

    private String getFormat()
    {
        return substringAfterLast( this.fileName, "." ).toLowerCase();
    }

    private int getBackgroundColor()
    {
        String value = backgroundColor;
        if ( isNotEmpty( value ) )
        {
            if ( value.startsWith( "0x" ) )
            {
                value = value.substring( 2 );
            }

            try
            {
                return Integer.parseInt( value, 16 );
            }
            catch ( Exception e )
            {
                // Do nothing
            }
        }
        return DEFAULT_BACKGROUND;
    }

    private int getQuality()
    {
        if ( isNullOrEmpty( this.quality ) )
        {
            return DEFAULT_QUALITY;
        }
        final Integer value = Ints.tryParse( this.quality );
        return ( value >= 0 ) && ( value <= 100 ) ? value : DEFAULT_QUALITY;
    }
}
