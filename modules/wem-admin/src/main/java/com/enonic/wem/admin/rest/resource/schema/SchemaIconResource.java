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

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

@Path("schema/icon")
@Produces("image/*")
public final class SchemaIconResource
{
    private static final String DEFAULT_MIME_TYPE = "image/png";

    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private MixinService mixinService;

    private ContentTypeIconResolver contentTypeIconResolver;

    private RelationshipTypeService relationshipTypeService;

    @GET
    @Path("{schemaKey}")
    public Response getSchemaIcon( @PathParam("schemaKey") final String schemaKeyAsString,
                                   @QueryParam("size") @DefaultValue("128") final int size, @QueryParam("hash") final String hash )
        throws Exception
    {
        final SchemaKey schemaKey = SchemaKey.from( schemaKeyAsString );

        final Icon icon = resolveSchemaIcon( schemaKey );

        Response.ResponseBuilder responseBuilder;
        if ( icon == null && schemaKey.isMixin() )
        {
            final BufferedImage defaultMixinImage = helper.getDefaultMixinImage( size );
            responseBuilder = Response.ok( defaultMixinImage, DEFAULT_MIME_TYPE );
        }
        else if ( icon == null && schemaKey.isRelationshipType() )
        {
            final BufferedImage defaultRelationshipTypeImage = helper.getDefaultRelationshipTypeImage( size );
            responseBuilder = Response.ok( defaultRelationshipTypeImage, DEFAULT_MIME_TYPE );
        }
        else if ( icon != null )
        {
            responseBuilder = Response.ok( helper.resizeImage( icon.asInputStream(), size ), icon.getMimeType() );
        }
        else
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        if ( StringUtils.isNotEmpty( hash ) )
        {
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge( Integer.MAX_VALUE );
            responseBuilder.cacheControl( cacheControl );
        }
        return responseBuilder.build();
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

    public Icon resolveContentTypeImage( final SchemaKey schemaKey )
    {
        return contentTypeIconResolver.resolve( ContentTypeName.from( schemaKey.getLocalName() ) );
    }

    private Icon resolveMixinImage( final SchemaKey schemaKey )
    {
        return findMixinIcon( MixinName.from( schemaKey.getLocalName() ) );
    }

    private Icon resolveRelationshipTypeImage( final SchemaKey schemaKey )
    {
        return findRelationshipTypeIcon( RelationshipTypeName.from( schemaKey.getLocalName() ) );
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
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
    }

}
