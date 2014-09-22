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
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

@Path("schema/icon")
@Produces("image/*")
public final class SchemaIconResource
{
    private static final String DEFAULT_MIME_TYPE = "image/png";

    private static final SchemaImageHelper helper = new SchemaImageHelper();

    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private MetadataSchemaService metadataSchemaService;

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
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        else if ( icon == null && schemaKey.isRelationshipType() )
        {
            final BufferedImage defaultRelationshipTypeImage = helper.getDefaultRelationshipTypeImage( size );
            responseBuilder = Response.ok( defaultRelationshipTypeImage, DEFAULT_MIME_TYPE );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
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
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    private Icon resolveSchemaIcon( final SchemaKey schemaKey )
    {
        final SchemaIconResolver schemaIconResolver =
            new SchemaIconResolver( contentTypeService, mixinService, relationshipTypeService, metadataSchemaService );
        if ( schemaKey.isContentType() )
        {
            return schemaIconResolver.resolveFromName( schemaKey.getName() );
        }
        else if ( schemaKey.isMixin() )
        {
            return schemaIconResolver.resolveFromName( schemaKey.getName() );
        }
        else if ( schemaKey.isRelationshipType() )
        {
            return schemaIconResolver.resolveFromName( schemaKey.getName() );
        }
        else if ( schemaKey.isMetadataSchema() )
        {
            return schemaIconResolver.resolveFromName( schemaKey.getName() );
        }

        else
        {
            throw new IllegalArgumentException( "Unknown SchemaKind: " + schemaKey.getType() );
        }
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
        this.contentTypeService = contentTypeService;
    }

}
