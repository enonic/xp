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

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class LayoutDescriptorResource
    implements JaxRsComponent
{
    private LayoutDescriptorService layoutDescriptorService;

    private LocaleService localeService;

    @GET
    public LayoutDescriptorJson getByKey( @QueryParam("key") final String layoutDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( layoutDescriptorKey );
        final LayoutDescriptor descriptor = layoutDescriptorService.getByKey( key );

        final LocaleMessageResolver localeMessageResolver = new LocaleMessageResolver( this.localeService, descriptor.getApplicationKey() );
        return new LayoutDescriptorJson( descriptor, localeMessageResolver );
    }

    @GET
    @Path("list/by_application")
    public LayoutDescriptorsJson getByApplications( @QueryParam("applicationKey") final String applicationKey )
    {
        final LayoutDescriptors descriptors = layoutDescriptorService.getByApplication( ApplicationKey.from( applicationKey ) );

        final LocaleMessageResolver localeMessageResolver =
            new LocaleMessageResolver( this.localeService, ApplicationKey.from( applicationKey ) );
        return new LayoutDescriptorsJson( descriptors, localeMessageResolver );
    }

    @POST
    @Path("list/by_applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public LayoutDescriptorsJson getByApplications( final GetByApplicationsParams params )
    {
        ImmutableList.Builder<LayoutDescriptorJson> layoutDescriptorsJsonBuilder = new ImmutableList.Builder();

        params.getApplicationKeys().forEach( applicationKey -> {
            layoutDescriptorsJsonBuilder.addAll( this.layoutDescriptorService.getByApplication( applicationKey ).
                stream().
                map( layoutDescriptor -> new LayoutDescriptorJson( layoutDescriptor,
                                                                   new LocaleMessageResolver( localeService, applicationKey ) ) ).
                collect( Collectors.toList() ) );
        } );

        return new LayoutDescriptorsJson( layoutDescriptorsJsonBuilder.build() );
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
