package com.enonic.xp.admin.impl.rest.resource.application;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.enonic.xp.admin.impl.market.MarketService;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconResolver;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.relationship.RelationshipTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.relationship.RelationshipTypeIconUrlResolver;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public class ApplicationResourceTest
    extends AdminResourceTestSupport
{
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

    private ApplicationIconUrlResolver iconUrlResolver;

    private RelationshipTypeIconUrlResolver relationshipTypeIconUrlResolver;

    private MacroIconUrlResolver macroIconUrlResolver;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

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

        mockContentTypes( applicationKey );
        mockPageDescriptors( applicationKey );
        mockPartDescriptors( applicationKey );
        mockLayoutDescriptors( applicationKey );
        mockRelationshipTypes( applicationKey );
        mockMacros( applicationKey );

        String response = request().
            path( "application/info" ).
            queryParam( "applicationKey", "testapplication" ).
            get().getAsString();
        assertJson( "get_application_info.json", response );
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
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return SiteDescriptor.create().form( config ).build();
    }

    private AuthDescriptor createAuthDescriptor()
    {
        final Form config = Form.create().
            addFormItem( Input.create().name( "some-name" ).label( "some-label" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        return AuthDescriptor.create().
            config( config ).
            build();
    }

    private void mockContentTypes( final ApplicationKey applicationKey )
    {
        final ContentType contentType =
            ContentType.create().name( ContentTypeName.media() ).form( Form.create().build() ).setAbstract().setFinal().allowChildContent(
                true ).setBuiltIn().contentDisplayNameScript( "contentDisplayNameScript" ).metadata( null ).displayName(
                "displayName" ).description( "description" ).modifiedTime( Instant.ofEpochSecond( 1000 ) ).createdTime( Instant.ofEpochSecond( 1000 ) ).creator(
                PrincipalKey.ofAnonymous() ).modifier( PrincipalKey.ofAnonymous() ).build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( contentTypeService.getByApplication( applicationKey ) ).thenReturn( contentTypes );
    }

    private void mockPageDescriptors( final ApplicationKey applicationKey )
    {
        final PageDescriptor pageDescriptor1 = PageDescriptor.create().
            displayName( "Landing page" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:landing-page" ) ).
            build();

        final PageDescriptor pageDescriptor2 = PageDescriptor.create().
            displayName( "Log out" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:logout-page" ) ).
            build();

        final PageDescriptors pageDescriptors = PageDescriptors.from( pageDescriptor1, pageDescriptor2 );
        Mockito.when( pageDescriptorService.getByApplication( applicationKey ) ).thenReturn( pageDescriptors );
    }

    private void mockPartDescriptors( final ApplicationKey applicationKey )
    {
        final PartDescriptor partDescriptor1 = PartDescriptor.create().
            displayName( "News part" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor partDescriptor2 = PartDescriptor.create().
            displayName( "News part2" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part2" ) ).
            build();

        Mockito.when( partDescriptorService.getByApplication( applicationKey ) ).
            thenReturn( PartDescriptors.from( partDescriptor1, partDescriptor2 ) );
    }

    private void mockLayoutDescriptors( final ApplicationKey applicationKey )
    {
        final LayoutDescriptor layoutDescriptor1 = LayoutDescriptor.create().
            displayName( "Fancy layout" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:fancy-layout" ) ).
            build();

        final LayoutDescriptor layoutDescriptor2 = LayoutDescriptor.create().
            displayName( "Putty layout" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "top" ).build() ).
                add( RegionDescriptor.create().name( "bottom" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:putty-layout" ) ).
            build();

        Mockito.when( layoutDescriptorService.getByApplication( applicationKey ) ).
            thenReturn( LayoutDescriptors.from( layoutDescriptor1, layoutDescriptor2 ) );

    }

    private void mockRelationshipTypes( final ApplicationKey applicationKey )
    {
        final RelationshipTypes relationshipTypes =
            RelationshipTypes.from( RelationshipType.create().name( "myapplication:person" ).build(),
                                    RelationshipType.create().name( "myapplication:site" ).build() );

        Mockito.when( this.relationshipTypeService.getByApplication( applicationKey ) ).thenReturn( relationshipTypes );
    }

    private void mockMacros( final ApplicationKey applicationKey )
    {
        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my-app1:macro1" ) ).
            description( "my description" ).
            displayName( "A macro" ).
            form( Form.create().build() ).
            build();

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.create().
            key( MacroKey.from( "my-app2:macro2" ) ).
            description( "my description" ).
            displayName( "B macro" ).
            form( Form.create().build() ).
            build();

        final MacroDescriptors macroDescriptors = MacroDescriptors.from( macroDescriptor1, macroDescriptor2 );

        Mockito.when(
            this.macroDescriptorService.getByApplications( ApplicationKeys.from( applicationKey, ApplicationKey.SYSTEM ) ) ).thenReturn(
            macroDescriptors );
    }


    @Override
    protected Object getResourceInstance()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.applicationDescriptorService = Mockito.mock( ApplicationDescriptorService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.marketService = Mockito.mock( MarketService.class );
        this.authDescriptorService = Mockito.mock( AuthDescriptorService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );

        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( new ContentTypeIconResolver( this.contentTypeService ) );
        this.relationshipTypeIconUrlResolver =
            new RelationshipTypeIconUrlResolver( new RelationshipTypeIconResolver( this.relationshipTypeService ) );
        this.macroIconUrlResolver = new MacroIconUrlResolver( new MacroIconResolver( this.macroDescriptorService ) );

        final ApplicationResource resource = new ApplicationResource();
        resource.setApplicationService( this.applicationService );
        resource.setSiteService( this.siteService );
        resource.setMarketService( this.marketService );
        resource.setAuthDescriptorService( this.authDescriptorService );
        resource.setApplicationDescriptorService( this.applicationDescriptorService );
        resource.setContentTypeService( this.contentTypeService );
        resource.setContentTypeIconUrlResolver( this.contentTypeIconUrlResolver );
        resource.setPageDescriptorService( this.pageDescriptorService );
        resource.setPartDescriptorService( this.partDescriptorService );
        resource.setLayoutDescriptorService( this.layoutDescriptorService );
        resource.setRelationshipTypeService( this.relationshipTypeService );
        resource.setMacroDescriptorService( this.macroDescriptorService );

        return resource;
    }
}
