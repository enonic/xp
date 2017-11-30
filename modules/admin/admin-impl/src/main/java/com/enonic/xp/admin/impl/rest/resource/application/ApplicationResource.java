package com.enonic.xp.admin.impl.rest.resource.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationDeploymentJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInfoJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstallParams;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstallResultJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationInstalledJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationListParams;
import com.enonic.xp.admin.impl.rest.resource.application.json.ApplicationSuccessJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ContentReferencesJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.GetMarketApplicationsJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.ListApplicationJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconResolver;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.relationship.RelationshipTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.relationship.RelationshipTypeIconUrlResolver;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

@Path(ResourceConstants.REST_ROOT + "application")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public final class ApplicationResource
    implements JaxRsComponent
{
    private final static String[] ALLOWED_PROTOCOLS = {"http", "https"};

    private final static Logger LOG = LoggerFactory.getLogger( ApplicationResource.class );

    private final Icon defaultAppIcon;

    private ApplicationService applicationService;

    private ApplicationDescriptorService applicationDescriptorService;

    private SiteService siteService;

    private MarketService marketService;

    private AuthDescriptorService authDescriptorService;

    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private RelationshipTypeService relationshipTypeService;

    private LayoutDescriptorService layoutDescriptorService;

    private MacroDescriptorService macroDescriptorService;

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private ResourceService resourceService;

    private PortalUrlService portalUrlService;

    private ApplicationIconUrlResolver iconUrlResolver;

    private RelationshipTypeIconUrlResolver relationshipTypeIconUrlResolver;

    private MacroIconUrlResolver macroIconUrlResolver;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    public ApplicationResource()
    {
        final byte[] image = loadDefaultImage( "app_default.svg" );
        defaultAppIcon = Icon.from( image, "image/svg+xml", Instant.ofEpochMilli( 0L ) );
        iconUrlResolver = new ApplicationIconUrlResolver();
    }

    @GET
    @Path("list")
    public ListApplicationJson list( @QueryParam("query") final String query )
    {
        Applications applications = this.applicationService.getInstalledApplications();

        applications = this.filterApplications( applications, query );
        applications = this.sortApplications( applications );

        final ListApplicationJson json = new ListApplicationJson();
        for ( final Application application : applications )
        {
            final ApplicationKey applicationKey = application.getKey();
            if ( !application.isSystem() )
            {
                final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );
                final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( applicationKey );
                final boolean localApplication = this.applicationService.isLocalApplication( applicationKey );
                final ApplicationDescriptor appDescriptor = this.applicationDescriptorService.get( applicationKey );

                json.add( application, localApplication, appDescriptor, siteDescriptor, authDescriptor, iconUrlResolver );
            }
        }

        return json;
    }

    @GET
    @Path("info")
    public ApplicationInfoJson info( @QueryParam("applicationKey") String key, @Context UriInfo ui )
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final ContentTypes contentTypes = contentTypeService.getByApplication( applicationKey );
        final ContentTypeSummaryListJson contentTypeSummaryListJson =
            new ContentTypeSummaryListJson( contentTypes, this.contentTypeIconUrlResolver );

        final PageDescriptors pageDescriptors = this.pageDescriptorService.getByApplication( applicationKey );
        final PageDescriptorListJson pageJson = new PageDescriptorListJson( PageDescriptors.from( pageDescriptors ) );

        final PartDescriptors partDescriptors = partDescriptorService.getByApplication( applicationKey );
        final PartDescriptorsJson partJson = new PartDescriptorsJson( partDescriptors );

        final LayoutDescriptors layoutDescriptors = layoutDescriptorService.getByApplication( applicationKey );
        final LayoutDescriptorsJson layoutJson = new LayoutDescriptorsJson( layoutDescriptors );

        final RelationshipTypes relationshipTypes = relationshipTypeService.getByApplication( applicationKey );
        final RelationshipTypeListJson relationshipTypeListJson =
            new RelationshipTypeListJson( relationshipTypes, this.relationshipTypeIconUrlResolver );

        final List<ApplicationKey> keys = Arrays.asList( applicationKey, ApplicationKey.SYSTEM );
        final MacrosJson macrosJson =
            new MacrosJson( this.macroDescriptorService.getByApplications( ApplicationKeys.from( keys ) ), this.macroIconUrlResolver );

        final ContentReferencesJson referencesJson =
            new ContentReferencesJson( this.contentService.findByApplicationKey( applicationKey ) );

        final Resource resource = resourceService.getResource( ResourceKey.from( applicationKey, "/main.js" ) );
        ApplicationDeploymentJson deploymentJson = null;
        if ( resource.exists() )
        {
            final String url = ui.getBaseUri() + "app/" + applicationKey.toString();
            deploymentJson = new ApplicationDeploymentJson( url, new URL( url ).getHost().equals( "localhost" ) );
        }

        return new ApplicationInfoJson().setContentTypes( contentTypeSummaryListJson ).
            setLayouts( layoutJson ).
            setMacros( macrosJson ).
            setPages( pageJson ).
            setParts( partJson ).
            setRelations( relationshipTypeListJson ).
            setReferences( referencesJson ).
            setDeployment( deploymentJson );
    }

    @GET
    @Path("listKeys")
    public List<String> listKeys( @QueryParam("query") final String query )
    {
        Applications applications = this.applicationService.getInstalledApplications();

        applications = this.filterApplications( applications, query );
        applications = this.sortApplications( applications );

        return applications.stream().filter( app -> !app.isSystem() ).map( app -> app.getKey().toString() ).
            collect( Collectors.toList() );
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
        final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( appKey );
        return new ApplicationJson( application, local, appDescriptor, siteDescriptor, authDescriptor, iconUrlResolver );
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
            LOG.error( failure = "Failed to upload application from " + urlString, e );
            result.setFailure( failure );
            return result;
        }
    }

    @GET
    @Path("icon/{appKey}")
    @Produces("image/*")
    public Response getIcon( @PathParam("appKey") final String appKeyStr, @QueryParam("hash") final String hash )
        throws Exception
    {
        final ApplicationKey appKey = ApplicationKey.from( appKeyStr );
        final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( appKey );
        final Icon icon = appDescriptor == null ? null : appDescriptor.getIcon();

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            responseBuilder = Response.ok( defaultAppIcon.asInputStream(), defaultAppIcon.getMimeType() );
            applyMaxAge( Integer.MAX_VALUE, responseBuilder );
        }
        else
        {
            responseBuilder = Response.ok( icon.toByteArray(), icon.getMimeType() );
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

    private ApplicationInstallResultJson installApplication( final URL url )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( url );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false, iconUrlResolver ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application from " + url;
            LOG.error( failure, e );

            result.setFailure( failure );
        }
        return result;
    }

    private ApplicationInstallResultJson installApplication( final ByteSource byteSource, final String applicationName )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( byteSource, applicationName );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false, iconUrlResolver ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application " + applicationName;
            LOG.error( failure, e );

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
        final String version = params.getVersion();
        final int start = params.getStart();
        final int count = params.getCount();
        final List<String> ids = params.getIds();

        return this.marketService.get( ids, version, start, count );
    }


    @GET
    @Path("getSiteApplications")
    public ListApplicationJson getSiteApplications( @QueryParam("query") final String query )
    {
        final ListApplicationJson json = new ListApplicationJson();

        Applications applications = this.applicationService.getInstalledApplications();

        applications = this.filterApplications( applications, query );
        applications = this.sortApplications( applications );

        for ( final Application application : applications )
        {
            final ApplicationKey applicationKey = application.getKey();
            final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );

            if ( siteDescriptor != null )
            {
                final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( applicationKey );
                final boolean localApplication = this.applicationService.isLocalApplication( applicationKey );
                final ApplicationDescriptor appDescriptor = this.applicationDescriptorService.get( applicationKey );
                json.add( application, localApplication, appDescriptor, siteDescriptor, authDescriptor, iconUrlResolver );
            }
        }
        return json;
    }

    @GET
    @Path("getIdProvider")
    public ApplicationJson getIdProvider( @QueryParam("applicationKey") String key )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( key );

        final AuthDescriptor authDescriptor = this.authDescriptorService.getDescriptor( applicationKey );

        if ( authDescriptor != null )
        {
            final Application application = this.applicationService.getInstalledApplication( applicationKey );
            final boolean localApplication = this.applicationService.isLocalApplication( applicationKey );

            final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );

            final ApplicationDescriptor appDescriptor = applicationDescriptorService.get( applicationKey );
            return new ApplicationJson( application, localApplication, appDescriptor, siteDescriptor, authDescriptor, iconUrlResolver );
        }
        return null;
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
                final ApplicationDescriptor appDescriptor = this.applicationDescriptorService.get( applicationKey );
                json.add( application, localApplication, appDescriptor, siteDescriptor, authDescriptor, iconUrlResolver );
            }
        }

        return json;
    }

    private byte[] loadDefaultImage( final String imageName )
    {
        try (final InputStream in = getClass().getResourceAsStream( imageName ))
        {
            if ( in == null )
            {
                throw new IllegalArgumentException( "Image [" + imageName + "] not found" );
            }

            return ByteStreams.toByteArray( in );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load default image: " + imageName, e );
        }
    }

    private Applications sortApplications( final Applications applications )
    {
        return Applications.from( applications.stream().
            sorted( ( app1, app2 ) -> app1.getDisplayName().compareTo( app2.getDisplayName() ) ).
            collect( Collectors.toList() ) );
    }

    private Applications filterApplications( final Applications applications, final String query )
    {
        if ( StringUtils.isNotBlank( query ) )
        {
            return Applications.from( applications.stream().
                filter( ( application ) -> containsIgnoreCase( application.getDisplayName(), query ) ||
                    containsIgnoreCase( application.getMaxSystemVersion(), query ) ||
                    containsIgnoreCase( application.getMinSystemVersion(), query ) ||
                    containsIgnoreCase( application.getSystemVersion(), query ) || containsIgnoreCase( application.getUrl(), query ) ||
                    containsIgnoreCase( application.getVendorName(), query ) || containsIgnoreCase( application.getVendorUrl(), query ) ).
                collect( Collectors.toList() ) );
        }

        return applications;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setApplicationDescriptorService( final ApplicationDescriptorService applicationDescriptorService )
    {
        this.applicationDescriptorService = applicationDescriptorService;
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

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( new ContentTypeIconResolver( contentTypeService ) );
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
        this.macroIconUrlResolver = new MacroIconUrlResolver( new MacroIconResolver( this.macroDescriptorService ) );
    }

    @Reference
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
        this.relationshipTypeIconUrlResolver =
            new RelationshipTypeIconUrlResolver( new RelationshipTypeIconResolver( relationshipTypeService ) );
    }
}

