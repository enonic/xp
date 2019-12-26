package com.enonic.xp.admin.impl.rest.resource.application;

import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.osgi.framework.Version;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.application.json.MarketAppVersionInfoJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationJson;
import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;
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
import com.enonic.xp.core.impl.app.ApplicationInstallException;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationResourceTest
    extends AdminResourceTestSupport
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApplicationService applicationService;

    private ApplicationDescriptorService applicationDescriptorService;

    private ApplicationInfoService applicationInfoService;

    private SiteService siteService;

    private MarketService marketService;

    private IdProviderDescriptorService idProviderDescriptorService;

    private RelationshipTypeService relationshipTypeService;

    private MacroDescriptorService macroDescriptorService;

    private ContentTypeService contentTypeService;

    private WidgetDescriptorService widgetDescriptorService;

    private AdminToolDescriptorService adminToolDescriptorService;

    private ResourceService resourceService;

    private PortalScriptService portalScriptService;

    private LocaleService localeService;

    private MixinService mixinService;

    @Test
    public void get_application_list()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );
        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );
        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

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
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, "/webapp/webapp.js" );

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

        final String deploymentUrl = MAPPER.readTree( response ).findPath( "deployment" ).findPath( "url" ).asText();
        assertEquals( "http://localhost:80/webapp/testapplication", deploymentUrl );
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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );
        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );

        Mockito.when( messageBundle.localize( "site.config.helpText" ) ).thenReturn( "translated.site.helpText" );
        Mockito.when( messageBundle.localize( "site.config.label" ) ).thenReturn( "translated.site.label" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

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
        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );
        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        String response = request().
            path( "application/getIdProviderApplications" ).
            get().getAsString();
        assertJson( "get_id_provider_applications.json", response );
    }

    @Test
    public void get_id_provider_application_empty()
        throws Exception
    {
        String response = request().
            path( "application/getIdProviderApplication" ).
            queryParam( "applicationKey", "nonexistentAppKey" ).
            get().getAsString();
        assertEquals( "", response );
    }

    @Test
    public void get_id_provider_applcation()
        throws Exception
    {
        final Application application = createApplication();
        Mockito.when( this.applicationService.getInstalledApplication( application.getKey() ) ).thenReturn( application );

        final IdProviderDescriptor idProviderDescriptor = createIdProviderDescriptor();
        Mockito.when( this.idProviderDescriptorService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn(
            idProviderDescriptor );

        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        String response = request().
            path( "application/getIdProviderApplication" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();
        assertJson( "get_id_provider_application.json", response );
    }

    @Test
    public void get_site_applications_validQuery()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );

        final SiteDescriptor siteDescriptor = createSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        final ApplicationDescriptor appDescriptor = createApplicationDescriptor();
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        Mockito.when( mixinService.inlineFormItems( Mockito.isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        String response = request().
            path( "application/getSiteApplications" ).
            queryParam( "query", "" ).
            get().getAsString();
        assertJson( "get_site_applications.json", response );
    }

    @Test
    public void get_site_applications_empty()
        throws Exception
    {
        final Application application = createApplication();
        final Applications applications = Applications.from( application );
        Mockito.when( this.applicationService.getInstalledApplications() ).thenReturn( applications );

        String response = request().
            path( "application/getSiteApplications" ).
            queryParam( "query", "skip all applications" ).
            get().getAsString();
        assertEquals( "{\"applications\":[],\"total\":0}", response );
    }

    @Test
    public void get_market_applications()
        throws Exception
    {
        final MarketApplicationJson marketApplicationJson = new MarketApplicationJson();
        marketApplicationJson.setName( "app1" );
        marketApplicationJson.setDisplayName( "market application displayname" );
        marketApplicationJson.setDescription( "market application description" );
        marketApplicationJson.setIconUrl( "http://market.enonic.com/app1" );
        marketApplicationJson.setVersions( Collections.singletonMap( "1", new MarketAppVersionInfoJson() ) );
        marketApplicationJson.setLatestVersion( "version-1.0" );

        final MarketApplicationsJson json = new MarketApplicationsJson();
        json.add( "appKey", marketApplicationJson );

        Mockito.when( this.marketService.get( Arrays.asList( "a", "b" ), "version-1.0", 0, 10 ) ).thenReturn( json );

        String response = request().
            path( "application/getMarketApplications" ).
            entity( "{\"version\":\"version-1.0\", \"start\":\"0\", \"count\":\"10\", \"ids\":[\"a\",\"b\"]}",
                    MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        assertJson( "get_market_applications.json", response );
    }

    @Test
    public void get_icon_default()
        throws Exception
    {
        String response = request().
            path( "application/icon/applicationKey" ).
            queryParam( "appKey", "applicationKey" ).
            queryParam( "hash", "123" ).
            get().getDataAsString();

        String expected = (String) Response.ok( readFromFile( "application.svg" ), "image/svg+xml" ).build().getEntity();

        assertEquals( expected, response );
    }

    @Test
    public void get_icon()
        throws Exception
    {
        final Icon icon = Icon.from( new byte[]{0, 1, 2}, "image/png", Instant.now() );

        final ApplicationDescriptor appDescriptor = createApplicationDescriptor( icon );
        Mockito.when( this.applicationDescriptorService.get( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( appDescriptor );

        byte[] response = request().
            path( "application/icon/applicationKey" ).
            queryParam( "appKey", "applicationKey" ).
            queryParam( "hash", "123" ).
            get().getData();

        byte[] expected = icon.toByteArray();

        assertTrue( Arrays.equals( expected, response ) );
    }

    @Test
    public void install_url_invalid_url()
        throws Exception
    {
        String response = request().
            path( "application/installUrl" ).
            entity( "{\"URL\":\"" + "http://enonic.net" + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void install_url_invalid_protocol()
        throws Exception
    {
        String response = request().
            path( "application/installUrl" ).
            entity( "{\"URL\":\"" + "inv://enonic.net" + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Failed to upload application from inv://enonic.net\"}", response );
    }

    @Test
    public void install_url_not_allowed_protocol()
        throws Exception
    {
        String response = request().
            path( "application/installUrl" ).
            entity( "{\"URL\":\"" + "ftp://enonic.net" + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Illegal protocol: ftp\"}", response );
    }

    @Test
    public void install_url()
        throws Exception
    {
        final Application application = createApplication();
        Mockito.when( this.applicationService.installGlobalApplication( new URL( application.getUrl() ) ) ).thenReturn( application );

        String response = request().
            path( "application/installUrl" ).
            entity( "{\"URL\":\"" + application.getUrl() + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        assertJson( "install_url.json", response );
    }

    @Test
    public void test_uninstall_failed()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "testapplication" );
        Mockito.doThrow( new ApplicationInstallException( "" ) ).when( this.applicationService ).uninstallApplication( applicationKey,
                                                                                                                       true );
        assertThrows(ApplicationInstallException.class, () -> {
            request().
                    path("application/uninstall").
                    entity("{\"key\":[\"" + applicationKey.toString() + "\"]}", MediaType.APPLICATION_JSON_TYPE).
                    post().getAsString();
        } );
    }

    @Test
    public void test_uninstall()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "testapplication" );

        final String response = request().
            path( "application/uninstall" ).
            entity( "{\"key\":[\"" + applicationKey.toString() + "\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{}", response );
    }

    @Test
    public void test_install_empty()
        throws Exception
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );
        Mockito.when( form.get( "file" ) ).thenReturn( null );

        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

        assertThrows(RuntimeException.class, () -> {
            request().path("application/install").multipart("file", "file.jar", new byte[]{0, 1, 2}, MediaType.MULTIPART_FORM_DATA_TYPE).
                    post();
        } );
    }

    @Test
    public void test_install_invalid()
        throws Exception
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem file = createItem( "file", 10, "jar", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( List.of( file ).iterator() );
        Mockito.when( form.get( "file" ) ).thenReturn( file );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

        Mockito.when( this.applicationService.installGlobalApplication( file.getBytes(), "file.jar" ) ).thenThrow( new RuntimeException() );

        String response = request().
            path( "application/install" ).multipart( "file", "file.jar", new byte[]{0, 1, 2}, MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertEquals( "{\"applicationInstalledJson\":null,\"failure\":\"Failed to process application file.jar\"}", response );
    }

    @Test
    public void test_install()
        throws Exception
    {

        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem file = createItem( "file", 10, "jar", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( List.of( file ).iterator() );
        Mockito.when( form.get( "file" ) ).thenReturn( file );
        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

        final Application application = createApplication();

        Mockito.when( this.applicationService.installGlobalApplication( file.getBytes(), "file.jar" ) ).thenReturn( application );

        String response = request().
            path( "application/install" ).multipart( "file", "file.jar", new byte[]{0, 1, 2}, MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "install_url.json", response );
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
        return createApplicationDescriptor( null );
    }

    private ApplicationDescriptor createApplicationDescriptor( final Icon icon )
    {
        return ApplicationDescriptor.create().
            key( ApplicationKey.from( "testapplication" ) ).
            description( "Application description" ).
            icon( icon ).
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

    private IdProviderDescriptor createIdProviderDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).labelI18nKey( "key.label" ).helpTextI18nKey(
                "key.help-text" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return IdProviderDescriptor.create().
            config( config ).
            build();
    }

    private Descriptors<WidgetDescriptor> createWidgetDescriptors()
    {
        final WidgetDescriptor widgetDescriptor1 = WidgetDescriptor.create().
            displayName( "My widget" ).
            description( "My widget description" ).
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

    private MultipartItem createItem( final String name, final long size, final String ext, final String type )
    {
        return createItem( name, name, size, ext, type );
    }

    private MultipartItem createItem( final String name, final String fileName, final long size, final String ext, final String type )
    {
        final MultipartItem item = Mockito.mock( MultipartItem.class );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getFileName() ).thenReturn( fileName + "." + ext );
        Mockito.when( item.getContentType() ).thenReturn( com.google.common.net.MediaType.parse( type ) );
        Mockito.when( item.getSize() ).thenReturn( size );
        Mockito.when( item.getBytes() ).thenReturn( ByteSource.wrap( name.getBytes() ) );
        return item;
    }

    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );
        this.applicationInfoService = Mockito.mock( ApplicationInfoService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.marketService = Mockito.mock( MarketService.class );
        this.idProviderDescriptorService = Mockito.mock( IdProviderDescriptorService.class );
        this.resourceService = Mockito.mock( ResourceService.class );
        this.portalScriptService = Mockito.mock( PortalScriptService.class );
        this.relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.localeService = Mockito.mock( LocaleService.class );
        this.widgetDescriptorService = Mockito.mock( WidgetDescriptorService.class );
        this.adminToolDescriptorService = Mockito.mock( AdminToolDescriptorService.class );
        this.mixinService = Mockito.mock( MixinService.class );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );
        resource.setMarketService( this.marketService );
        resource.setIdProviderDescriptorService( this.idProviderDescriptorService );
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
        resource.setMixinService( this.mixinService );

        return resource;
    }
}
