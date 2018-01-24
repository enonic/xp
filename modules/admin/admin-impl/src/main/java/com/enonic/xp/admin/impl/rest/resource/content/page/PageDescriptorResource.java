package com.enonic.xp.admin.impl.rest.resource.content.page;

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

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.page.part.GetByApplicationsParams;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "content/page/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class PageDescriptorResource
    implements JaxRsComponent
{
    private PageDescriptorService pageDescriptorService;

    private LocaleService localeService;

    @GET
    public PageDescriptorJson getByKey( @QueryParam("key") final String pageDescriptorKey )
    {
        final DescriptorKey key = DescriptorKey.from( pageDescriptorKey );
        final PageDescriptor descriptor = pageDescriptorService.getByKey( key );

        final LocaleMessageResolver localeMessageResolver = new LocaleMessageResolver( this.localeService, key.getApplicationKey() );
        final PageDescriptorJson json = new PageDescriptorJson( descriptor, localeMessageResolver );
        return json;
    }

    @GET
    @Path("list/by_application")
    public PageDescriptorListJson getByApplication( @QueryParam("applicationKey") final String applicationKey )
    {
        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByApplication( ApplicationKey.from( applicationKey ) );

        final LocaleMessageResolver localeMessageResolver =
            new LocaleMessageResolver( this.localeService, ApplicationKey.from( applicationKey ) );
        return new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ), localeMessageResolver );
    }

    @POST
    @Path("list/by_applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageDescriptorListJson getByApplications( final GetByApplicationsParams params )
    {
        ImmutableList.Builder<PageDescriptorJson> pageDescriptorsJsonBuilder = new ImmutableList.Builder();

        params.getApplicationKeys().forEach( applicationKey -> {
            pageDescriptorsJsonBuilder.addAll( this.pageDescriptorService.getByApplication( applicationKey ).
                stream().
                map( pageDescriptor -> new PageDescriptorJson( pageDescriptor,
                                                               new LocaleMessageResolver( localeService, applicationKey ) ) ).
                collect( Collectors.toList() ) );
        } );

        return new PageDescriptorListJson( pageDescriptorsJsonBuilder.build() );
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
