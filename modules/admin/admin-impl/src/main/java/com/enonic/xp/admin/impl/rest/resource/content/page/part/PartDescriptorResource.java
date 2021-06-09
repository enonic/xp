package com.enonic.xp.admin.impl.rest.resource.content.page.part;

import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.content.JsonObjectsFactory;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.security.RoleKeys;

import static com.google.common.base.Strings.isNullOrEmpty;

@Path(ResourceConstants.REST_ROOT + "content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class PartDescriptorResource
    implements JaxRsComponent
{
    private PartDescriptorService partDescriptorService;

    private JsonObjectsFactory jsonObjectsFactory;

    private static final PartImageHelper HELPER = new PartImageHelper();

    @GET
    public PartDescriptorJson getByKey( @QueryParam("key") final String partDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( partDescriptorKey );
        final PartDescriptor descriptor = partDescriptorService.getByKey( key );

        return jsonObjectsFactory.createPartDescriptorJson( descriptor );
    }

    @GET
    @Path("list/by_application")
    public PartDescriptorsJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        return new PartDescriptorsJson( partDescriptorService.getByApplication( ApplicationKey.from( applicationKey ) )
                                            .stream()
                                            .map( jsonObjectsFactory::createPartDescriptorJson )
                                            .collect( Collectors.toUnmodifiableList() ) );
    }


    @POST
    @Path("list/by_applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public PartDescriptorsJson getByApplications( final GetByApplicationsParams params )
    {
        return new PartDescriptorsJson( this.partDescriptorService.getByApplications( params.getApplicationKeys() )
                                            .stream()
                                            .map( jsonObjectsFactory::createPartDescriptorJson )
                                            .collect( Collectors.toUnmodifiableList() ) );
    }

    @GET
    @Path("icon/{partKey}")
    @Produces("image/*")
    public Response getIcon( @PathParam("partKey") final String partKey, @QueryParam("size") @DefaultValue("128") final int size,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( partKey );
        final PartDescriptor partDescriptor = this.partDescriptorService.getByKey( descriptorKey );

        Response.ResponseBuilder responseBuilder;

        final Icon icon = partDescriptor.getIcon();
        if ( icon == null )
        {
            final byte[] defaultMixinImage = HELPER.getDefaultPartImage();
            responseBuilder = Response.ok( defaultMixinImage, "image/svg+xml" );
        }
        else
        {
            final byte[] image = HELPER.readIconImage( icon, size );
            responseBuilder = Response.ok( image, icon.getMimeType() );
        }

        if ( !isNullOrEmpty( hash ) )
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

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setJsonObjectsFactory( final JsonObjectsFactory jsonObjectsFactory )
    {
        this.jsonObjectsFactory = jsonObjectsFactory;
    }
}
