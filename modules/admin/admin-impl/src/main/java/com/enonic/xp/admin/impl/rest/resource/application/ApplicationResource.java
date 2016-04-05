package com.enonic.xp.admin.impl.rest.resource.application;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.json.application.ApplicationJson;
import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstallParams;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstallResultJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstalledJson;
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
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

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

    private final static String[] ALLOWED_PROTOCOLS = {"http", "https"};

    private final static Logger LOG = LoggerFactory.getLogger( ApplicationResource.class );

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
            if ( !ApplicationKey.from( "com.enonic.xp.admin.ui" ).equals( applicationKey ) )//Remove after 7.0.0 refactoring
            {
                json.add( application, this.applicationService.isLocalApplication( applicationKey ),
                          this.siteService.getDescriptor( applicationKey ) );
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
        return new ApplicationJson( application, local, siteDescriptor );
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
    @Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ApplicationInstallResultJson install( final MultipartForm form )
        throws Exception
    {
        final MultipartItem appFile = form.get( "file" );

        if ( appFile == null )
        {
            throw new RuntimeException( "Missing file item" );
        }

        final ByteSource byteSource = appFile.getBytes();

        return installApplication( byteSource, appFile.getFileName() );
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
    @Path("installUrl")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
        throws Exception
    {
        final String urlString = params.getURL();
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();
        String failure;
        try
        {
            final URL url = new URL( urlString );

            if ( ArrayUtils.contains( ALLOWED_PROTOCOLS, url.getProtocol() ) )
            {
                ApplicationInstallResultJson json = installApplication( url );

                return json;
            }
            else
            {
                failure = "Illegal protocol: " + url.getProtocol();
                result.setFailure( failure );

                return result;
            }

        }
        catch ( IOException e )
        {
            LOG.error( failure = "Failed to upload application from " + urlString );
            result.setFailure( failure );
            return result;
        }
    }

    private ApplicationInstallResultJson installApplication( final URL url )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( url );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application from " + url;
            LOG.error( failure );

            result.setFailure( failure );
        }
        return result;
    }

    private ApplicationInstallResultJson installApplication( final ByteSource byteSource, final String applicationName )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( byteSource );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application " + applicationName;
            LOG.error( failure );

            result.setFailure( failure );
        }
        return result;
    }

    @POST
    @Path("getMarketApplications")
    @Consumes(MediaType.APPLICATION_JSON)
    public MarketApplicationsJson getMarketApplications( final GetMarketApplicationsJson params )
        throws Exception
    {
        String version = params.getVersion() != null ? params.getVersion() : "1.0.0";

        return this.marketService.get( version );
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
}

