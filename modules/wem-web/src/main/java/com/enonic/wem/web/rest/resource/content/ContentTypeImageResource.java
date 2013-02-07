package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

import static com.enonic.wem.api.command.Commands.contentType;

@Component
@Path("content-type/image")
@Produces("image/*")
public final class ContentTypeImageResource
{
    private final ContentTypeImageHelper helper;

    private Client client;

    public ContentTypeImageResource()
        throws Exception
    {
        this.helper = new ContentTypeImageHelper();
    }

    @GET
    @Path("{contentTypeName}")
    public Response getContentTypeIcon( @PathParam("contentTypeName") final String contentTypeQualifiedName,
                                        @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        ContentType contentType = getContentType( new QualifiedContentTypeName( contentTypeQualifiedName ) );
        while ( contentType != null && contentType.getIcon() == null )
        {
            final QualifiedContentTypeName contentTypeName = contentType.getSuperType();
            contentType = getContentType( contentTypeName );
        }
        final Icon contentTypeIcon = contentType == null ? null : contentType.getIcon();

        final BufferedImage contentTypeImage = this.helper.getContentTypeIcon( contentTypeIcon, size );
        if ( contentTypeImage == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
        return Response.ok( contentTypeImage, contentTypeIcon.getMimeType() ).build();
    }

    private ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        final QualifiedContentTypeNames contentTypeNames = QualifiedContentTypeNames.from( contentTypeName );
        return this.client.execute( contentType().get().names( contentTypeNames ) ).first();
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
