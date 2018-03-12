package com.enonic.xp.admin.impl.rest.resource.application;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationInfo;
import com.enonic.xp.app.ApplicationInfoService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ApplicationResourceTest
    extends AdminResourceTestSupport
{
    private ApplicationService applicationService;

    private ApplicationDescriptorService applicationDescriptorService;

    private ApplicationInfoService applicationInfoService;

    private SiteService siteService;

    private MarketService marketService;

    private AuthDescriptorService authDescriptorService;

    private RelationshipTypeService relationshipTypeService;

    private MacroDescriptorService macroDescriptorService;

    private ContentTypeService contentTypeService;

    private WidgetDescriptorService widgetDescriptorService;

    private AdminToolDescriptorService adminToolDescriptorService;

    private ResourceService resourceService;

    private PortalScriptService portalScriptService;

    private LocaleService localeService;

    @Test
    public void get_application_list()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/list" ).
            get().getAsString();
        assertJson( "get_application_list_success.json", response );
    }

    @Test
    public void get_application_info()
        throws Exception
    {
        final ApplicationKey applicationKey = createApplication().getKey();
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/main.js" );

        final ApplicationInfo applicationInfo = ApplicationInfo.create().build();

        Mockito.when( this.applicationInfoService.getApplicationInfo( applicationKey ) ).thenReturn( applicationInfo );

        final Resource resource = Mockito.mock( Resource.class );
        Mockito.when( resource.exists() ).thenReturn( true );
        Mockito.when( resource.getKey() ).thenReturn( resourceKey );
        Mockito.when( this.resourceService.getResource( resourceKey ) ).thenReturn( resource );

        final ScriptExports scriptExports = Mockito.mock( ScriptExports.class );
        Mockito.when( scriptExports.hasMethod( "get" ) ).thenReturn( true );
        Mockito.when( this.portalScriptService.execute( resourceKey ) ).thenReturn( scriptExports );

        Mockito.when( this.widgetDescriptorService.getByApplication( applicationKey ) ).thenReturn( createWidgetDescriptors() );

        final AdminToolDescriptors adminToolDescriptors = createAdminToolDescriptors();
        Mockito.when( this.adminToolDescriptorService.getByApplication( applicationKey ) ).thenReturn( adminToolDescriptors );
        Mockito.when( this.adminToolDescriptorService.generateAdminToolUri( Mockito.any(), Mockito.any() ) ).thenReturn( "url/to/tool" );

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ResteasyProviderFactory.getContextDataMap().put( HttpServletRequest.class, mockRequest );

        final String response = request().
            path( "application/info" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();

        assertJson( "get_application_info.json", response );

        final String deploymentUrl = new ObjectMapper().readTree( response ).findPath( "deployment" ).findPath( "url" ).asText();
        assertEquals( "http://localhost:80/app/testapplication", deploymentUrl );
    }

    @Test
    public void get_application_list_with_query()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application, createEmptyApplication() );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/list" ).
            queryParam( "query", "Enonic" ).
            get().getAsString();
        assertJson( "get_application_list_success.json", response );
    }

    @Test
    public void get_application_list_with_invalid_query()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application, createEmptyApplication() );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/list" ).
            queryParam( "query", "invalid query" ).
            get().getAsString();
        assertJson( "get_application_list_with_invalid_query.json", response );
    }

    @Test
    public void get_application_keys()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/listKeys" ).
            get().getAsString();
        assertJson( "get_application_keys_success.json", response );
    }

    @Test
    public void get_application_by_key()
        throws Exception
    {
        final Application application = createApplication();
        Mockito.when( this.applicationService.getInstalledApplication( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( application );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();
        assertJson( "get_application_by_key_success.json", response );
    }

    @Test
    public void get_application_i18n()
        throws Exception
    {
        final Application application = createApplication();
        Mockito.when( this.applicationService.getInstalledApplication( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( application );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );

        Mockito.when( messageBundle.localize( "site.config.helpText" ) ).thenReturn( "translated.site.helpText" );
        Mockito.when( messageBundle.localize( "site.config.label" ) ).thenReturn( "translated.site.label" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        String response = request().
            path( "application" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();
        assertJson( "get_application_i18n.json", response );
    }

    @Test
    public void start_application()
        throws Exception
    {
        request().
            path( "application/start" ).
            entity( "{\"key\":[\"testapplication\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).startApplication( ApplicationKey.from( "testapplication" ), true );
    }

    @Test
    public void stop_application()
        throws Exception
    {
        request().
            path( "application/stop" ).
            entity( "{\"key\":[\"testapplication\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( this.applicationService ).stopApplication( ApplicationKey.from( "testapplication" ), true );
    }

    @Test
    public void get_id_provider_applications()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final AuthDescriptor authDescriptor = createAuthDescriptor();
        Mockito.when( this.authDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( authDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        String response = request().
            path( "application/getIdProviderApplications" ).
            get().getAsString();
        assertJson( "get_id_provider_applications.json", response );
    }

    private Application createApplication()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        Mockito.when( application.getVersion() ).thenReturn( new Version( 1, 0, 0 ) );
        Mockito.when( application.getDisplayName() ).thenReturn( "application display name" );
        Mockito.when( application.getUrl() ).thenReturn( "http://enonic.net" );
        Mockito.when( application.getVendorName() ).thenReturn( "Enonic" );
        Mockito.when( application.getVendorUrl() ).thenReturn( "https://www.enonic.com" );
        Mockito.when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        Mockito.when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }

    private ApplicationDescriptor createApplicationDescriptor()
    {
        return ApplicationDescriptor.create().
            key( ApplicationKey.from( "testapplication" ) ).
            description( "Application description" ).
            build();
    }

    private Application createEmptyApplication()
    {
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getDisplayName() ).thenReturn( "empty name" );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "empty_testapplication" ) );
        return application;
    }

    private SiteDescriptor createSiteDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).helpTextI18nKey( "site.config.helpText" ).labelI18nKey(
                "site.config.label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();

        return SiteDescriptor.create().form( config ).build();
    }

    private AuthDescriptor createAuthDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).labelI18nKey( "key.label" ).helpTextI18nKey(
                "key.help-text" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return AuthDescriptor.create().
            config( config ).
            build();
    }

    private Descriptors<WidgetDescriptor> createWidgetDescriptors()
    {
        final WidgetDescriptor widgetDescriptor1 = WidgetDescriptor.create().
            displayName( "My widget" ).
            addInterface( "com.enonic.xp.my-interface" ).
            addInterface( "com.enonic.xp.my-interface-2" ).
            key( DescriptorKey.from( "myapp:my-widget" ) ).
            build();

        return Descriptors.from( widgetDescriptor1 );
    }

    private AdminToolDescriptors createAdminToolDescriptors()
    {
        final AdminToolDescriptor adminToolDescriptor = AdminToolDescriptor.create().
            key( DescriptorKey.from( "myapp:my-tool" ) ).
            displayName( "My tool" ).
            build();

        Mockito.when( this.adminToolDescriptorService.getIconByKey( adminToolDescriptor.getKey() ) ).thenReturn( "icon-source" );

        return AdminToolDescriptors.from( adminToolDescriptor );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );
        this.applicationInfoService = Mockito.mock( ApplicationInfoService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.marketService = Mockito.mock( MarketService.class );
        this.authDescriptorService = Mockito.mock( AuthDescriptorService.class );
        this.resourceService = Mockito.mock( ResourceService.class );
        this.portalScriptService = Mockito.mock( PortalScriptService.class );
        this.relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.localeService = Mockito.mock( LocaleService.class );
        this.widgetDescriptorService = Mockito.mock( WidgetDescriptorService.class );
        this.adminToolDescriptorService = Mockito.mock( AdminToolDescriptorService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );
        resource.setMarketService( this.marketService );
        resource.setAuthDescriptorService( this.authDescriptorService );
        resource.setApplicationDescriptorService( this.applicationDescriptorService );
        resource.setApplicationInfoService( this.applicationInfoService );
        resource.setContentTypeService( this.contentTypeService );
        resource.setRelationshipTypeService( this.relationshipTypeService );
        resource.setMacroDescriptorService( this.macroDescriptorService );
        resource.setResourceService( this.resourceService );
        resource.setPortalScriptService( this.portalScriptService );
        resource.setLocaleService( this.localeService );
        resource.setWidgetDescriptorService( this.widgetDescriptorService );
        resource.setAdminToolDescriptorService( this.adminToolDescriptorService );

        return resource;
    }
}
