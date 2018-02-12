package com.enonic.xp.admin.impl.rest.resource.content.page.part;

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

import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class PartDescriptorResource
    implements JaxRsComponent
{
    private PartDescriptorService partDescriptorService;

    private LocaleService localeService;

    @GET
    public PartDescriptorJson getByKey( @QueryParam("key") final String partDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( partDescriptorKey );
        final PartDescriptor descriptor = partDescriptorService.getByKey( key );

        final LocaleMessageResolver localeMessageResolver = new LocaleMessageResolver( this.localeService, descriptor.getApplicationKey() );
        return new PartDescriptorJson( descriptor, localeMessageResolver );
    }

    @GET
    @Path("list/by_application")
    public PartDescriptorsJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final PartDescriptors descriptors = partDescriptorService.getByApplication( ApplicationKey.from( applicationKey ) );

        final LocaleMessageResolver localeMessageResolver =
            new LocaleMessageResolver( this.localeService, ApplicationKey.from( applicationKey ) );
        return new PartDescriptorsJson( descriptors, localeMessageResolver );
    }


    @POST
    @Path("list/by_applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public PartDescriptorsJson getByApplications( final GetByApplicationsParams params )
    {
        ImmutableList.Builder<PartDescriptorJson> partDescriptorsJsonBuilder = new ImmutableList.Builder();

        params.getApplicationKeys().forEach( applicationKey -> {
            partDescriptorsJsonBuilder.addAll( this.partDescriptorService.getByApplication( applicationKey ).
                stream().
                map( partDescriptor -> new PartDescriptorJson( partDescriptor,
                                                               new LocaleMessageResolver( localeService, applicationKey ) ) ).
                collect( Collectors.toList() ) );
        } );
        return new PartDescriptorsJson( partDescriptorsJsonBuilder.build() );
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
