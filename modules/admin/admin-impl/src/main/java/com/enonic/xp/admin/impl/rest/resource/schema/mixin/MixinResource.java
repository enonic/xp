package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import com.enonic.xp.admin.impl.json.schema.mixin.MixinJson;
import com.enonic.xp.admin.impl.json.schema.mixin.MixinListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.SchemaImageHelper;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.security.RoleKeys;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ResourceConstants.REST_ROOT + "schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class MixinResource
    implements JaxRsComponent
{
    private static final String DEFAULT_MIME_TYPE = "image/svg+xml";

    private static final SchemaImageHelper HELPER = new SchemaImageHelper();

    private MixinService mixinService;

    private MixinIconUrlResolver mixinIconUrlResolver;

    private MixinIconResolver mixinIconResolver;

    @GET
    public MixinJson get( @QueryParam("name") final String name )
    {
        final MixinName mixinName = MixinName.from( name );
        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin == null )
        {
            throw new WebApplicationException( String.format( "Mixin [%s] was not found.", mixinName ), Response.Status.NOT_FOUND );
        }

        return new MixinJson( mixin, this.mixinIconUrlResolver );
    }

    @GET
    @Path("list")
    public MixinListJson list()
    {
        final Mixins mixins = mixinService.getAll();
        return new MixinListJson( mixins, this.mixinIconUrlResolver );
    }

    @GET
    @Path("byApplication")
    public MixinListJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final Mixins mixins = mixinService.getByApplication( ApplicationKey.from( applicationKey ) );
        return new MixinListJson( mixins, this.mixinIconUrlResolver );
    }

    @GET
    @Path("icon/{mixinName}")
    @Produces("image/*")
    public Response getIcon( @PathParam("mixinName") final String mixinNameStr, @QueryParam("size") @DefaultValue("128") final int size,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final MixinName mixinName = MixinName.from( mixinNameStr );
        final Icon icon = this.mixinIconResolver.resolveIcon( mixinName );

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final byte[] defaultMixinImage = HELPER.getDefaultMixinImage();
            responseBuilder = Response.ok( defaultMixinImage, DEFAULT_MIME_TYPE );
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

    private Mixin fetchMixin( final MixinName name )
    {
        return mixinService.getByName( name );
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
        this.mixinIconResolver = new MixinIconResolver( mixinService );
        this.mixinIconUrlResolver = new MixinIconUrlResolver( this.mixinIconResolver );
    }
}
