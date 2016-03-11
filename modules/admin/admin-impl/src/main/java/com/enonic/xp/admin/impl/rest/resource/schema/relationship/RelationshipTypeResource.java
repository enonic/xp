package com.enonic.xp.admin.impl.rest.resource.schema.relationship;

import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeJson;
import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.SchemaImageHelper;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.RoleKeys;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ResourceConstants.REST_ROOT + "schema/relationship")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class RelationshipTypeResource
    implements JaxRsComponent
{
    private static final String DEFAULT_MIME_TYPE = "image/svg+xml";

    private static final SchemaImageHelper HELPER = new SchemaImageHelper();

    private RelationshipTypeService relationshipTypeService;

    private RelationshipTypeIconUrlResolver relationshipTypeIconUrlResolver;

    private RelationshipTypeIconResolver relationshipTypeIconResolver;

    @GET
    public RelationshipTypeJson get( @QueryParam("name") final String name )
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( name );
        final RelationshipType relationshipType = fetchRelationshipType( relationshipTypeName );

        if ( relationshipType == null )
        {
            String message = String.format( "RelationshipType [%s] was not found.", relationshipTypeName );
            throw new WebApplicationException( message, Response.Status.NOT_FOUND );
        }

        return new RelationshipTypeJson( relationshipType, this.relationshipTypeIconUrlResolver );
    }

    public RelationshipType fetchRelationshipType( final RelationshipTypeName name )
    {
        return relationshipTypeService.getByName( name );
    }

    @GET
    @Path("list")
    public RelationshipTypeListJson list()
    {
        final RelationshipTypes relationshipTypes = relationshipTypeService.getAll();

        return new RelationshipTypeListJson( relationshipTypes, this.relationshipTypeIconUrlResolver );
    }

    @GET
    @Path("byApplication")
    public RelationshipTypeListJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final RelationshipTypes relationshipTypes = relationshipTypeService.getByApplication( ApplicationKey.from( applicationKey ) );

        return new RelationshipTypeListJson( relationshipTypes, this.relationshipTypeIconUrlResolver );
    }

    @GET
    @Path("icon/{relationshipTypeName}")
    @Produces("image/*")
    public Response getIcon( @PathParam("relationshipTypeName") final String relationshipTypeStr,
                             @QueryParam("size") @DefaultValue("128") final int size, @QueryParam("hash") final String hash )
        throws Exception
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( relationshipTypeStr );
        final Icon icon = this.relationshipTypeIconResolver.resolveIcon( relationshipTypeName );

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final byte[] defaultRelationshipTypeImage = HELPER.getDefaultRelationshipTypeImage();
            responseBuilder = Response.ok( defaultRelationshipTypeImage, DEFAULT_MIME_TYPE );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        else
        {
            final Object image = HELPER.isSvg( icon ) ? icon.toByteArray() : HELPER.resizeImage( icon.asInputStream(), size );
            responseBuilder = Response.ok( image, icon.getMimeType() );
            if ( StringUtils.isNotEmpty( hash ) )
            {
                applyMaxAge( Integer.MAX_VALUE, responseBuilder );
            }
        }

        return responseBuilder.build();
    }

    private void applyMaxAge( int maxAge, final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( maxAge );
        responseBuilder.cacheControl( cacheControl );
    }

    @Reference
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
        this.relationshipTypeIconResolver = new RelationshipTypeIconResolver( relationshipTypeService );
        this.relationshipTypeIconUrlResolver = new RelationshipTypeIconUrlResolver( this.relationshipTypeIconResolver );
    }
}
