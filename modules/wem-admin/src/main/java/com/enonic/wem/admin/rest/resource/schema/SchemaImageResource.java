package com.enonic.wem.admin.rest.resource.schema;

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
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.command.Commands.relationshipType;


@Path("schema/image")
@Produces("image/*")
public final class SchemaImageResource
{
    private final SchemaImageHelper helper;

    private Client client;

    public SchemaImageResource()
        throws Exception
    {
        this.helper = new SchemaImageHelper();
    }

    @GET
    @Path("{schemaKey}")
    public Response getSchemaIcon( @PathParam("schemaKey") final String schemaKeyAsString,
                                   @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final SchemaKey schemaKey = SchemaKey.from( schemaKeyAsString );

        String mimeType = "image/png";
        BufferedImage schemaImage = null;
        if ( schemaKey.isContentType() )
        {
            final Icon contentTypeIcon = findRootContentTypeIcon( ContentTypeName.from( schemaKey.getLocalName() ) );
            schemaImage = helper.getIconImage( contentTypeIcon, size );
            mimeType = contentTypeIcon == null ? mimeType : contentTypeIcon.getMimeType();
        }
        else if ( schemaKey.isRelationshipType() )
        {
            final Icon relationshipTypeIcon = findRelationshipTypeIcon( RelationshipTypeName.from( schemaKey.getLocalName() ) );
            if ( relationshipTypeIcon == null )
            {
                schemaImage = helper.getDefaultRelationshipTypeImage( size );
            }
            else
            {
                schemaImage = helper.getIconImage( relationshipTypeIcon, size );
                mimeType = relationshipTypeIcon.getMimeType();
            }
        }
        else if ( schemaKey.isMixin() )
        {
            final Icon mixinIcon = findMixinIcon( MixinName.from( schemaKey.getLocalName() ) );
            if ( mixinIcon == null )
            {
                schemaImage = helper.getDefaultMixinImage( size );
            }
            else
            {
                schemaImage = helper.getIconImage( mixinIcon, size );
                mimeType = mixinIcon.getMimeType();
            }
        }

        if ( schemaImage == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
        return Response.ok( schemaImage, mimeType ).build();
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
        return client.execute( contentType().get(). byNames().contentTypeNames( contentTypeNames ) ).first();
    }

    private Icon findMixinIcon( final MixinName mixinName )
    {
        final Mixin mixin = client.execute( mixin().get().byName( mixinName ) );
        return mixin == null ? null : mixin.getIcon();
    }

    private Icon findRelationshipTypeIcon( final RelationshipTypeName relationshipTypeName )
    {
        final RelationshipTypeNames relationshipTypeNames = RelationshipTypeNames.from( relationshipTypeName );
        RelationshipType relationshipType = client.execute( relationshipType().get().names( relationshipTypeNames ) ).first();
        return relationshipType == null ? null : relationshipType.getIcon();

    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
