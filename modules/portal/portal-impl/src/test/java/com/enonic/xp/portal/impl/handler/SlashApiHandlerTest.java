package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.Project;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlashApiHandlerTest
{
    private SlashApiHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private ApiDescriptorService apiDescriptorService;

    private ExceptionRenderer exceptionRenderer;

    private SiteService siteService;

    private WebappService webappService;

    private AdminToolDescriptorService adminToolDescriptorService;

    private DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private PortalRequest request;

    @BeforeEach
    void setUp()
        throws IOException
    {
        controllerScriptFactory = mock( ControllerScriptFactory.class );
        apiDescriptorService = mock( ApiDescriptorService.class );
        exceptionRenderer = mock( ExceptionRenderer.class );
        siteService = mock( SiteService.class );
        webappService = mock( WebappService.class );
        adminToolDescriptorService = mock( AdminToolDescriptorService.class );
        universalApiHandlerRegistry = new DynamicUniversalApiHandlerRegistry();

        handler =
            new SlashApiHandler( controllerScriptFactory, apiDescriptorService, new ExceptionMapperImpl(), exceptionRenderer, siteService,
                                 webappService, adminToolDescriptorService, universalApiHandlerRegistry );

        when( this.exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        final WebSocketConfig webSocketConfig = mock( WebSocketConfig.class );

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn(
            PortalResponse.create().webSocket( webSocketConfig ).build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        final WebSocketContext webSocketContext = mock( WebSocketContext.class );
        when( webSocketContext.apply( any( WebSocketEndpoint.class ) ) ).thenReturn( true );

        request = new PortalRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( null );
        request.setWebSocketContext( webSocketContext );
        request.setRawPath( "/api/com.enonic.app.myapp:api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );
    }

    @AfterEach
    void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    void testInvalidPattern()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/path/to/some/resource" );

        // path must start with `/api/` or contains `/_/` as endpoint part
        IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid API path: /path/to/some/resource", ex.getMessage() );
    }

    @Test
    void testHttpOptions()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.OPTIONS );

        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp:api-key" );

        WebResponse res = this.handler.handle( webRequest );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );

        when( webRequest.getEndpointPath() ).thenReturn( "/api/com.enonic.app.myapp:api-key" );

        res = this.handler.handle( webRequest );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testHandleApiAccessDenied()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp:api-key" );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( applicationKey, "api-key" ) )
            .allowedPrincipals( PrincipalKeys.create().add( PrincipalKey.from( "role:principalKey" ) ).build() )
            .build();

        when( apiDescriptorService.getByKey( eq( DescriptorKey.from( applicationKey, "api" ) ) ) ).thenReturn( null );
        when( apiDescriptorService.getByKey( eq( DescriptorKey.from( applicationKey, "api-key" ) ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "You don't have permission to access \"api-key\" API for \"com.enonic.app.myapp\"", ex.getMessage() );
    }

    @Test
    void testHandleApi()
        throws Exception
    {
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp:api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
            .mount( true )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );

        assertNull( request.getSite() );
        assertNull( request.getContent() );
        assertEquals( "/api/com.enonic.app.myapp:api-key", request.getContextPath() );
    }

    @Test
    void testHandleApiWhenApiDisabled()
    {
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp:api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
            .mount( false )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void testHandleApiWhenApiDescriptorNotFound()
    {
        request.setEndpointPath( null );
        request.setRawPath( "/api/app:unknown" );

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );

        WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void testHandleApiWithoutTracer()
        throws Exception
    {
        Tracer.setManager( null );
        testHandleApi();
    }

    @Test
    void testHandleApiError()
        throws Exception
    {
        when( this.exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenThrow( new NullPointerException() );

        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp:api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
            .mount( true )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, webResponse.getStatus() );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        request.setMethod( HttpMethod.CONNECT );
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp:api-key" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    @Test
    void testSiteMountApplicationDoesNotInstalledToSiteAndProject()
    {
        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp:api-key" );
        request.setRawPath( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "master" ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "api-key" );

        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( descriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn( SiteConfigs.empty() );

        request.setSite( site );

        final SiteConfigs projectConfigs = mock( SiteConfigs.class );
        when( projectConfigs.get( eq( applicationKey ) ) ).thenReturn(
            SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() );

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( SiteConfigs.empty() );

        request.setProject( project );

        WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void testSiteMountSiteDescriptorNotFound()
    {
        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp:api-key" );
        request.setRawPath( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "master" ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "api-key" );

        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( descriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn(
            SiteConfigs.create().add( SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() ).build() );

        request.setSite( site );

        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( null );

        WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void testSiteMountApiDoesNotDefinedIdApis()
    {
        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp:api-key" );
        request.setRawPath( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "master" ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "api-key" );

        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( descriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn(
            SiteConfigs.create().add( SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() ).build() );

        request.setSite( site );

        final DescriptorKeys siteApiMountDescriptors =
            DescriptorKeys.from( DescriptorKey.from( applicationKey, "api-key-1" ), DescriptorKey.from( applicationKey, "api-key-2" ) );

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().apiMounts( siteApiMountDescriptors ).build();
        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( siteDescriptor );

        WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }

    @Test
    void testSiteMountApiDefinedApis()
        throws Exception
    {
        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp:api-key-1" );
        request.setRawPath( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key-1" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "master" ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "api-key-1" );

        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( descriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn(
            SiteConfigs.create().add( SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() ).build() );

        request.setSite( site );

        final DescriptorKeys siteApiMountDescriptors =
            DescriptorKeys.from( DescriptorKey.from( applicationKey, "api-key-1" ), DescriptorKey.from( applicationKey, "api-key-2" ) );

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().apiMounts( siteApiMountDescriptors ).build();
        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( siteDescriptor );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertEquals( site, request.getSite() );
        assertNull( request.getContent() );
        assertEquals( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key-1", request.getContextPath() );
    }

    @Test
    void testSiteMountApiDefinedIdApis()
        throws Exception
    {
        request.setBaseUri( "/site" );
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp:api-key-1" );
        request.setRawPath( "/site/project/master/mysite/_/com.enonic.app.myapp:api-key-1" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "master" ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "api-key-1" );

        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( descriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn(
            SiteConfigs.create().add( SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() ).build() );

        request.setSite( site );

        final DescriptorKeys siteApiMountDescriptors =
            DescriptorKeys.from( DescriptorKey.from( applicationKey, "api-key-1" ), DescriptorKey.from( applicationKey, "api-key-2" ) );

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().apiMounts( siteApiMountDescriptors ).build();
        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( siteDescriptor );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testSiteEndpointWithInvalidApplicationName()
    {
        request.setBaseUri( "/site" );
        request.setEndpointPath( "/_/<>:api-key" );
        request.setRawPath( "/site/project/branch/mysite/_/<>:api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Application key [<>] not found", ex.getMessage() );
    }

    @Test
    void testWebappMountExternalAPI()
        throws Exception
    {
        final ApplicationKey webappApplicationKey = ApplicationKey.from( "com.enonic.app.mywebapp" );
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final DescriptorKey apiInExternalApp = DescriptorKey.from( apiApplicationKey, "myapi" );

        final WebappDescriptor webappDescriptor =
            WebappDescriptor.create().applicationKey( webappApplicationKey ).apiMounts( DescriptorKeys.from( apiInExternalApp ) ).build();

        when( webappService.getDescriptor( eq( webappApplicationKey ) ) ).thenReturn( webappDescriptor );

        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.myapp:myapi" );
        request.setRawPath( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp:myapi" );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        // test it on not root webapp path
        request.setEndpointPath( "/_/com.enonic.app.myapp:myapi" );
        request.setRawPath( "/webapp/com.enonic.app.mywebapp/path/_/com.enonic.app.myapp:myapi" );

        response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testWebappMountExternalApiIsNotMountedToWebapp()
    {
        final ApplicationKey webappApplicationKey = ApplicationKey.from( "com.enonic.app.mywebapp" );
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        final WebappDescriptor webappDescriptor =
            WebappDescriptor.create().applicationKey( webappApplicationKey ).apiMounts( DescriptorKeys.empty() ).build();

        when( webappService.getDescriptor( eq( webappApplicationKey ) ) ).thenReturn( webappDescriptor );

        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.myapp:myapi" );
        request.setRawPath( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testWebappMountNoWebappDescriptor()
    {
        final ApplicationKey webappApplicationKey = ApplicationKey.from( "com.enonic.app.mywebapp" );
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.myapp" );

        when( webappService.getDescriptor( eq( webappApplicationKey ) ) ).thenReturn( null );

        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.myapp:myapi" );
        request.setRawPath( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testWebappMountInvalidUrl()
    {
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );
        request.setEndpointPath( "/_/com.enonic.app.myapp:myapi" );
        request.setRawPath( "/webapp/_/com.enonic.app.myapp:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testAdminMount()
        throws Exception
    {
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "mytool" );
        final AdminToolDescriptor toolDescriptor = AdminToolDescriptor.create()
            .displayName( "My Tool" )
            .key( descriptorKey )
            .apiMounts( DescriptorKeys.from( DescriptorKey.from( apiApplicationKey, "myapi" ) ) )
            .build();

        when( adminToolDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( toolDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/mytool/_/com.enonic.app.external.app:myapi" );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testAdminMountApiDoesNotMount()
    {
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "mytool" );
        final AdminToolDescriptor toolDescriptor =
            AdminToolDescriptor.create().displayName( "My Tool" ).key( descriptorKey ).apiMounts( DescriptorKeys.empty() ).build();

        when( adminToolDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( toolDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/mytool/_/com.enonic.app.external.app:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testAdminMountToolDescriptorDoesNotExists()
    {
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "mytool" );
        when( adminToolDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( null );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/mytool/_/com.enonic.app.external.app:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testAdminMountInvalidUrlPattern()
    {
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/_/com.enonic.app.external.app:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testUnsupportedUrl()
    {
        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( apiApplicationKey, "myapi" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/unsupported/path/_/com.enonic.app.external.app:myapi" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testAddAndRemoveDynamicApiHandler()
        throws Exception
    {
        final MyUniversalApiHandler myUniversalApiHandler = new MyUniversalApiHandler();
        universalApiHandlerRegistry.addApiHandler( myUniversalApiHandler,
                                                   Map.of( "applicationKey", "com.enonic.app.external.app", "apiKey", "myapi",
                                                           "allowedPrincipals", RoleKeys.EVERYONE.toString() ) );

        final ApplicationKey apiApplicationKey = ApplicationKey.from( "com.enonic.app.external.app" );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "mytool" );
        final AdminToolDescriptor toolDescriptor = AdminToolDescriptor.create()
            .displayName( "My Tool" )
            .key( descriptorKey )
            .apiMounts( DescriptorKeys.from( DescriptorKey.from( apiApplicationKey, "myapi" ) ) )
            .build();

        when( adminToolDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( toolDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/mytool/_/com.enonic.app.external.app:myapi" );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "Body", response.getBody().toString() );

        universalApiHandlerRegistry.removeApiHandler( myUniversalApiHandler );
    }

    @Test
    void testHandleDynamicApiHandlerWhichDoesNotMountToAPI()
    {
        final MyUniversalApiHandler myUniversalApiHandler = new MyUniversalApiHandler();
        universalApiHandlerRegistry.addApiHandler( myUniversalApiHandler,
                                                   Map.of( "applicationKey", "com.enonic.app.external.app", "apiKey", "myapi",
                                                           "allowedPrincipals", RoleKeys.EVERYONE.toString() ) );

        final ApplicationKey applicationKey = ApplicationKey.from( "com.enonic.app.myapp" );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, "mytool" );
        final AdminToolDescriptor toolDescriptor =
            AdminToolDescriptor.create().displayName( "My Tool" ).key( descriptorKey ).apiMounts( DescriptorKeys.empty() ).build();

        when( adminToolDescriptorService.getByKey( eq( descriptorKey ) ) ).thenReturn( toolDescriptor );

        request.setEndpointPath( "/_/com.enonic.app.external.app:myapi" );
        request.setRawPath( "/admin/com.enonic.app.myapp/mytool/_/com.enonic.app.external.app:myapi" );

        final WebException exception = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );

        universalApiHandlerRegistry.removeApiHandler( myUniversalApiHandler );
    }

    @Test
    void testDynamicApiHandlerWithWebSocket()
        throws Exception
    {
        WSUniversalApiHandler wsUniversalApiHandler = new WSUniversalApiHandler();
        universalApiHandlerRegistry.addApiHandler( wsUniversalApiHandler,
                                                   Map.of( "applicationKey", "myapp", "apiKey", "myapi", "allowedPrincipals",
                                                           RoleKeys.EVERYONE.toString(), "mount", "true" ) );

        final ApplicationKey apiApplicationKey = ApplicationKey.from( "myapp" );

        request.setApplicationKey( apiApplicationKey );
        request.setEndpointPath( null );
        request.setRawPath( "/api/myapp:myapi" );
        request.setWebSocketContext( endpoint -> {
            endpoint.onEvent( WebSocketEvent.create().build() );
            return true;
        } );

        assertEquals( 0, wsUniversalApiHandler.getCounter() );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertEquals( 1, wsUniversalApiHandler.getCounter() );
    }

    @Test
    void testMediaImplicitMountedOnSite()
        throws Exception
    {
        final DescriptorKey apiDescriptorKey = DescriptorKey.from( ApplicationKey.from( "media" ), "image" );
        final ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( apiDescriptorKey ).allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) ).build();

        when( apiDescriptorService.getByKey( eq( apiDescriptorKey ) ) ).thenReturn( apiDescriptor );

        request.setEndpointPath( "/_/media:image" );
        request.setRawPath( "/site/repo/branch/path/_/media:image/id/scale/name" );

        WebResponse response = this.handler.handle( request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertNull( request.getSite() );
        assertNull( request.getContent() );
    }

    private static final class MyUniversalApiHandler
        implements UniversalApiHandler
    {
        @Override
        public WebResponse handle( final WebRequest request )
        {
            return WebResponse.create().body( "Body" ).build();
        }
    }

    private static final class WSUniversalApiHandler
        implements UniversalApiHandler
    {
        private static long counter = 0;

        @Override
        public WebResponse handle( final WebRequest request )
        {
            final WebSocketConfig webSocket = new WebSocketConfig();
            webSocket.setSubProtocols( List.of( "sub-protocol" ) );
            webSocket.setData( Map.of( "k", "v" ) );

            return WebResponse.create().webSocket( webSocket ).build();
        }

        @Override
        public void onSocketEvent( final WebSocketEvent event )
        {
            counter++;
        }

        public long getCounter()
        {
            return counter;
        }
    }
}
