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
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;

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
            final Icon contentTypeIcon =
                findRootContentTypeIcon( new QualifiedContentTypeName( schemaKey.getModuleName(), schemaKey.getLocalName() ) );
            schemaImage = helper.getIconImage( contentTypeIcon, size );
            mimeType = contentTypeIcon == null ? mimeType : contentTypeIcon.getMimeType();
        }
        else if ( schemaKey.isRelationshipType() )
        {
            final Icon relationshipTypeIcon =
                findRelationshipTypeIcon( new QualifiedRelationshipTypeName( schemaKey.getModuleName(), schemaKey.getLocalName() ) );
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
            final Icon mixinIcon = findMixinIcon( new QualifiedMixinName( schemaKey.getModuleName(), schemaKey.getLocalName() ) );
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

    private Icon findRootContentTypeIcon( final QualifiedContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType == null ? null : contentType.getIcon();
    }

    private ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentTypeName );
        return client.execute( contentType().get().qualifiedNames( qualifiedNames ) ).first();
    }

    private Icon findMixinIcon( final QualifiedMixinName mixinName )
    {
        final QualifiedMixinNames mixinNames = QualifiedMixinNames.from( mixinName );
        Mixin mixin = client.execute( mixin().get().names( mixinNames ) ).first();
        return mixin == null ? null : mixin.getIcon();
    }

    private Icon findRelationshipTypeIcon( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        final QualifiedRelationshipTypeNames relationshipTypeNames = QualifiedRelationshipTypeNames.from( relationshipTypeName );
        RelationshipType relationshipType = client.execute( relationshipType().get().qualifiedNames( relationshipTypeNames ) ).first();
        return relationshipType == null ? null : relationshipType.getIcon();

    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
