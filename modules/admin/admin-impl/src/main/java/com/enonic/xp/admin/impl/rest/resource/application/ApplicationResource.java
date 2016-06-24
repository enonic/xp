package com.enonic.xp.admin.impl.rest.resource.application;

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

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationListParams;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationSuccessJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.GetMarketApplicationsJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ListApplicationJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

@Path(ResourceConstants.REST_ROOT + "application")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class ApplicationResource
    implements JaxRsComponent
{
    private ApplicationService applicationService;

    private SiteService siteService;

    private MarketService marketService;

    private AuthDescriptorService authDescriptorService;

    @GET
    @Path("list")
    public ListApplicationJson list( @QueryParam("query") final String query )
    {
        Applications applications = this.applicationService.getInstalledApplications();
        if ( StringUtils.isNotBlank( query ) )
        {
            applications = Applications.from( applications.stream().
                filter( ( application ) -> containsIgnoreCase( application.getDisplayName(), query ) ||
                    containsIgnoreCase( application.getMaxSystemVersion(), query ) ||
                    containsIgnoreCase( application.getMinSystemVersion(), query ) ||
                    containsIgnoreCase( application.getSystemVersion(), query ) ||
                    containsIgnoreCase( application.getUrl(), query ) ||
                    containsIgnoreCase( application.getVendorName(), query ) ||
                    containsIgnoreCase( application.getVendorUrl(), query ) ).
                collect( Collectors.toList() ) );
        }

        final ListApplicationJson json = new ListApplicationJson();
        for ( final Application application : applications )
        {
            if ( !application.hasSiteDescriptor() )
            {
                continue;
            }
            final ApplicationKey applicationKey = application.getKey();
            if ( !ApplicationKey.from( "com.enonic.xp.admin.ui" ).equals( applicationKey ) &&
                !ApplicationKey.from( "com.enonic.xp.app.standardidprovider" ).equals(
                    applicationKey ) )//TODO Remove after 7.0.0 refactoring
            {
                final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );
                final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( applicationKey );
                final boolean localApplication = this.applicationService.isLocalApplication( applicationKey );

                json.add( application, localApplication, siteDescriptor, authDescriptor );
            }
        }

        return json;
    }

    @GET
    public ApplicationJson getByKey( @QueryParam("applicationKey") String applicationKey )
    {
        final ApplicationKey appKey = ApplicationKey.from( applicationKey );
        final Application application = this.applicationService.getInstalledApplication( appKey );

        if ( application == null )
        {
            throw new ApplicationNotFoundException( appKey );
        }

        final boolean local = this.applicationService.isLocalApplication( appKey );
        final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( appKey );
        final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( appKey );
        return new ApplicationJson( application, local, siteDescriptor, authDescriptor );
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationSuccessJson start( final ApplicationListParams params )
        throws Exception
    {
        params.getKeys().forEach( ( key ) -> this.applicationService.startApplication( key, true ) );
        return new ApplicationSuccessJson();
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationSuccessJson stop( final ApplicationListParams params )
        throws Exception
    {
        params.getKeys().forEach( ( key ) -> this.applicationService.stopApplication( key, true ) );
        return new ApplicationSuccessJson();
    }

    @POST
    @Path("uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationSuccessJson uninstall( final ApplicationListParams params )
        throws Exception
    {
        params.getKeys().forEach( applicationKey -> this.applicationService.uninstallApplication( applicationKey, true ) );
        return new ApplicationSuccessJson();
    }

    @POST
    @Path("getMarketApplications")
    @Consumes(MediaType.APPLICATION_JSON)
    public MarketApplicationsJson getMarketApplications( final GetMarketApplicationsJson params )
        throws Exception
    {
        final String version = params.getVersion() != null ? params.getVersion() : "1.0.0";
        final int start = parseInt( params.getStart(), 0 );
        final int count = parseInt( params.getCount(), 10 );

        return this.marketService.get( version, start, count );
    }

    @GET
    @Path("getIdProviderApplications")
    public ListApplicationJson getIdProviderApplications()
    {
        final ListApplicationJson json = new ListApplicationJson();

        Applications applications = this.applicationService.getInstalledApplications();
        for ( final Application application : applications )
        {
            final ApplicationKey applicationKey = application.getKey();
            final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( applicationKey );

            if ( authDescriptor != null )
            {
                final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );
                final boolean localApplication = this.applicationService.isLocalApplication( applicationKey );
                json.add( application, localApplication, siteDescriptor, authDescriptor );
            }
        }

        return json;
    }

    private int parseInt( final String value, final int defaultValue )
    {
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            return defaultValue;
        }
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

    @Reference
    public void setMarketService( final MarketService marketService )
    {
        this.marketService = marketService;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }
}

