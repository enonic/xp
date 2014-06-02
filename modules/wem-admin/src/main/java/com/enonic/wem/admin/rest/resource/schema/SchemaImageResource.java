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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

@Path("schema/image")
@Produces("image/*")
public final class SchemaImageResource
{
    private static final String DEFAULT_MIME_TYPE = "image/png";

    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private MixinService mixinService;

    private ContentTypeService contentTypeService;

    private RelationshipTypeService relationshipTypeService;

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Inject
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @GET
    @Path("{schemaKey}")
    public Response getSchemaIcon( @PathParam("schemaKey") final String schemaKeyAsString,
                                   @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( 3600 );

        final SchemaKey schemaKey = SchemaKey.from( schemaKeyAsString );

        final Icon icon = resolveSchemaIcon( schemaKey );

        if ( icon == null && schemaKey.isMixin() )
        {
            final BufferedImage defaultMixinImage = helper.getDefaultMixinImage( size );
            return Response.ok( defaultMixinImage, DEFAULT_MIME_TYPE ).cacheControl( cacheControl ).build();
        }
        else if ( icon == null && schemaKey.isRelationshipType() )
        {
            final BufferedImage defaultRelationshipTypeImage = helper.getDefaultRelationshipTypeImage( size );
            return Response.ok( defaultRelationshipTypeImage, DEFAULT_MIME_TYPE ).cacheControl( cacheControl ).build();
        }
        else if ( icon != null )
        {
            return Response.ok( helper.resizeImage( icon.asInputStream(), size ), icon.getMimeType() ).cacheControl( cacheControl ).build();
        }
        else
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
    }

    private Icon resolveSchemaIcon( final SchemaKey schemaKey )
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

    private Icon resolveContentTypeImage( final SchemaKey schemaKey )
    {
        return findContentTypeIcon( ContentTypeName.from( schemaKey.getLocalName() ) );
    }

    private Icon resolveMixinImage( final SchemaKey schemaKey )
    {
        return findMixinIcon( MixinName.from( schemaKey.getLocalName() ) );
    }

    private Icon resolveRelationshipTypeImage( final SchemaKey schemaKey )
    {
        return findRelationshipTypeIcon( RelationshipTypeName.from( schemaKey.getLocalName() ) );
    }

    private Icon findContentTypeIcon( final ContentTypeName contentTypeName )
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

    private Icon findMixinIcon( final MixinName mixinName )
    {
        final Mixin mixin = mixinService.getByName( new GetMixinParams( mixinName ) );
        return mixin == null ? null : mixin.getIcon();
    }

    private Icon findRelationshipTypeIcon( final RelationshipTypeName relationshipTypeName )
    {
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( relationshipTypeName );
        final RelationshipType relationshipType = relationshipTypeService.getByName( params );
        return relationshipType == null ? null : relationshipType.getIcon();
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( ContentTypeNames.from( contentTypeName ) );

        return contentTypeService.getByNames( params ).first();
    }

}
