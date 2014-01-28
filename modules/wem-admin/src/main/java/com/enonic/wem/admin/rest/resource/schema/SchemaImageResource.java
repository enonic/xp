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
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.command.Commands.relationshipType;


@Path("schema/image")
@Produces("image/*")
public final class SchemaImageResource
{
    public static final String DEFAULT_MIME_TYPE = "image/png";

    private SchemaImageHelper helper;

    private Client client;

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
        this.helper = new SchemaImageHelper( client );
    }

    @GET
    @Path("{schemaKey}")
    public Response getSchemaIcon( @PathParam("schemaKey") final String schemaKeyAsString,
                                   @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final SchemaKey schemaKey = SchemaKey.from( schemaKeyAsString );

        final SchemaIcon icon = resolveSchemaIcon( schemaKey );

        if ( icon == null && schemaKey.isMixin() )
        {
            final BufferedImage defaultMixinImage = helper.getDefaultMixinImage( size );
            return Response.ok( defaultMixinImage, DEFAULT_MIME_TYPE ).build();
        }
        else if ( icon == null && schemaKey.isRelationshipType() )
        {
            final BufferedImage defaultRelationshipTypeImage = helper.getDefaultRelationshipTypeImage( size );
            return Response.ok( defaultRelationshipTypeImage, DEFAULT_MIME_TYPE ).build();
        }
        else if ( icon != null )
        {
            return Response.ok( helper.resizeImage( icon.asInputStream(), size ), icon.getMimeType() ).build();
        }
        else
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
    }

    private SchemaIcon resolveSchemaIcon( final SchemaKey schemaKey )
    {
        if ( schemaKey.isContentType() )
        {
            return resolveContentTypeImage( schemaKey );
        }
        else if ( schemaKey.isMixin() )
        {
            return resolveMixinImage( schemaKey );
        }
        else if ( schemaKey.isRelationshipType() )
        {
            return resolveRelationshipTypeImage( schemaKey );
        }
        else
        {
            return null;
        }
    }

    private SchemaIcon resolveContentTypeImage( final SchemaKey schemaKey )
    {
        return findContentTypeIcon( ContentTypeName.from( schemaKey.getLocalName() ) );
    }

    private SchemaIcon resolveMixinImage( final SchemaKey schemaKey )
    {
        return findMixinIcon( MixinName.from( schemaKey.getLocalName() ) );
    }

    private SchemaIcon resolveRelationshipTypeImage( final SchemaKey schemaKey )
    {
        return findRelationshipTypeIcon( RelationshipTypeName.from( schemaKey.getLocalName() ) );
    }

    private SchemaIcon findContentTypeIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        if ( contentType == null )
        {
            return null;
        }
        else if ( contentType.getIcon() != null )
        {
            return contentType.getIcon();
        }

        do
        {
            contentType = getContentType( contentType.getSuperType() );
            if ( contentType.getIcon() != null )
            {
                return contentType.getIcon();
            }
        }
        while ( contentType != null );
        return null;
    }

    private SchemaIcon findMixinIcon( final MixinName mixinName )
    {
        final Mixin mixin = client.execute( mixin().get().byName( mixinName ) );
        return mixin == null ? null : mixin.getIcon();
    }

    private SchemaIcon findRelationshipTypeIcon( final RelationshipTypeName relationshipTypeName )
    {
        final RelationshipType relationshipType = client.execute( relationshipType().get().byName( relationshipTypeName ) );
        return relationshipType == null ? null : relationshipType.getIcon();
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return client.execute( contentType().get().byNames().contentTypeNames( ContentTypeNames.from( contentTypeName ) ) ).first();
    }

}
