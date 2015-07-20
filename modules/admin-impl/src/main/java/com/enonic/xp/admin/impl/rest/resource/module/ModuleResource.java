package com.enonic.xp.admin.impl.rest.resource.module;

import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.json.module.ModuleJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.module.json.ListModuleJson;
import com.enonic.xp.admin.impl.rest.resource.module.json.ModuleListParams;
import com.enonic.xp.admin.impl.rest.resource.module.json.ModuleSuccessJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

@Path(ResourceConstants.REST_ROOT + "module")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ModuleResource
    implements AdminResource
{
    private ApplicationService applicationService;

    private SiteService siteService;

    @GET
    @Path("list")
    public ListModuleJson list( @QueryParam("query") final String query )
    {
        Applications applications = this.applicationService.getAllModules();
        final ImmutableList.Builder<SiteDescriptor> siteDescriptors = ImmutableList.builder();
        if ( StringUtils.isNotBlank( query ) )
        {
            applications = Applications.from( applications.stream().
                filter( ( module ) -> containsIgnoreCase( module.getDisplayName(), query ) ||
                    containsIgnoreCase( module.getMaxSystemVersion(), query ) ||
                    containsIgnoreCase( module.getMinSystemVersion(), query ) ||
                    containsIgnoreCase( module.getSystemVersion(), query ) ||
                    containsIgnoreCase( module.getUrl(), query ) ||
                    containsIgnoreCase( module.getVendorName(), query ) ||
                    containsIgnoreCase( module.getVendorUrl(), query ) ).
                collect( Collectors.toList() ) );
        }

        for ( Application application : applications )
        {
            siteDescriptors.add( this.siteService.getDescriptor( application.getKey() ) );
        }

        return new ListModuleJson( applications, siteDescriptors.build() );
    }

    @GET
    public ModuleJson getByKey( @QueryParam("applicationKey") String applicationKey )
    {
        final Application application = this.applicationService.getModule( ApplicationKey.from( applicationKey ) );
        final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( ApplicationKey.from( applicationKey ) );
        return new ModuleJson( application, siteDescriptor );
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson start( final ModuleListParams params )
        throws Exception
    {
        params.getApplicationKeys().forEach( this.applicationService::startModule );
        return new ModuleSuccessJson();
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public ModuleSuccessJson stop( final ModuleListParams params )
        throws Exception
    {
        params.getApplicationKeys().forEach( this.applicationService::stopModule );
        return new ModuleSuccessJson();
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

}

