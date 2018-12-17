package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.security.RoleKeys;

import static com.enonic.xp.web.servlet.ServletRequestUrlHelper.contentDispositionAttachment;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "content/media")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class ContentMediaResource
    implements JaxRsComponent
{
    private ContentService contentService;

    @GET
    @Path("{contentId}")
    public Response media( @PathParam("contentId") final String contentIdAsString )
        throws IOException
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        return doServeMedia( contentId, null );
    }

    @GET
    @Path("{contentId}/{identifier}")
    public Response media( @PathParam("contentId") final String contentIdAsString, @PathParam("identifier") final String identifier )
        throws IOException
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        return doServeMedia( contentId, identifier );
    }

    @GET
    @Path("preview/{contentId}")
    public Response previewMedia( @PathParam("contentId") final String contentIdAsString )
        throws IOException
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        return doServeMedia( contentId, null, true );
    }

    @GET
    @Path("preview/{contentId}/{identifier}")
    public Response previewMedia( @PathParam("contentId") final String contentIdAsString, @PathParam("identifier") final String identifier )
        throws IOException
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        return doServeMedia( contentId, identifier, true );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("isAllowPreview")
    public boolean isAllowPreview( @QueryParam("contentId") final String contentIdAsString,
                                   @QueryParam("identifier") final String identifier )
    {
        final ContentId contentId = ContentId.from( contentIdAsString );
        final Attachment attachment = resolveAttachment( identifier, contentId );

        if ( attachment == null )
        {
            return false;
        }

        return attachmentAllowsPreview( attachment );
    }

    private Response doServeMedia( final ContentId contentId, final String identifier, final Boolean preview )
        throws IOException
    {
        final Attachment attachment = resolveAttachment( identifier, contentId );
        if ( attachment == null )
        {
            throw JaxRsExceptions.notFound( String.format( "Content [%s] has no attachments", contentId ) );
        }
        else if ( preview && !attachmentAllowsPreview( attachment ) )
        {
            throw new WebApplicationException( String.format( "Preview for attachment [%s] is not supported", attachment.getName() ) );
        }

        final ByteSource binary = contentService.getBinary( contentId, attachment.getBinaryReference() );
        Response.ResponseBuilder response = Response.ok( binary.openStream(), attachment.getMimeType() );

        if ( !preview )
        {
            final String fileName = attachment.getName();
            if ( isNotEmpty( fileName ) )
            {
                response = response.header( "Content-Disposition", contentDispositionAttachment( fileName ) );
            }
        }
        return response.build();
    }

    private Response doServeMedia( final ContentId contentId, final String identifier )
        throws IOException
    {
        return doServeMedia( contentId, identifier, false );
    }

    private Boolean attachmentAllowsPreview( final Attachment attachment )
    {
        return !attachment.getMimeType().startsWith( "application/msword" ) && !attachment.getMimeType().startsWith( "application/vnd." );
    }

    private Attachment resolveAttachment( final String identifier, final ContentId contentId )
    {
        final Content content = contentService.getById( contentId );

        if ( content == null )
        {
            throw JaxRsExceptions.notFound( String.format( "Content [%s] was not found", contentId ) );
        }

        return resolveAttachment( identifier, content );
    }

    private Attachment resolveAttachment( final String identifier, final Content content )
    {
        Attachment attachment = null;
        if ( isNotEmpty( identifier ) )
        {
            attachment = content.getAttachments().byName( identifier );
            if ( attachment == null )
            {
                attachment = content.getAttachments().byLabel( identifier );
            }
        }
        if ( content.getType().isDescendantOfMedia() && attachment == null )
        {
            attachment = ( (Media) content ).getSourceAttachment();
        }
        return attachment;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
