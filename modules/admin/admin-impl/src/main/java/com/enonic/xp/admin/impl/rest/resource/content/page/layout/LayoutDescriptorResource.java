package com.enonic.xp.admin.impl.rest.resource.content.page.layout;

import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.content.JsonObjectsFactory;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class LayoutDescriptorResource
    implements JaxRsComponent
{
    private LayoutDescriptorService layoutDescriptorService;

    private JsonObjectsFactory jsonObjectsFactory;

    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String layoutDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( layoutDescriptorKey );
        final LayoutDescriptor descriptor = layoutDescriptorService.getByKey( key );

        return jsonObjectsFactory.createLayoutDescriptorJson( descriptor );
    }

    @GET
    @Path("list/by_application")
    public LayoutDescriptorsJson getByApplications( @QueryParam("applicationKey") final String applicationKey )
    {
        final LayoutDescriptors descriptors = layoutDescriptorService.getByApplication( ApplicationKey.from( applicationKey ) );

        return new LayoutDescriptorsJson(
            descriptors.stream().map( jsonObjectsFactory::createLayoutDescriptorJson ).collect( Collectors.toUnmodifiableList() ) );
    }

    @POST
    @Path("list/by_applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public LayoutDescriptorsJson getByApplications( final GetByApplicationsParams params )
    {
        final LayoutDescriptors layoutDescriptors = layoutDescriptorService.getByApplications( params.getApplicationKeys() );
        return new LayoutDescriptorsJson(
            layoutDescriptors.stream().map( jsonObjectsFactory::createLayoutDescriptorJson ).collect( Collectors.toUnmodifiableList() ) );
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setJsonObjectsFactory( final JsonObjectsFactory jsonObjectsFactory )
    {
        this.jsonObjectsFactory = jsonObjectsFactory;
    }
}
