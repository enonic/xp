package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiContextPath;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiMount;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlashApiHandlerTest
{
    private SlashApiHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private ApiDescriptorService apiDescriptorService;

    private ExceptionRenderer exceptionRenderer;

    private ContentService contentService;

    private ProjectService projectService;

    private PortalRequest request;

    @BeforeEach
    public void setUp()
        throws IOException
    {
        controllerScriptFactory = mock( ControllerScriptFactory.class );
        apiDescriptorService = mock( ApiDescriptorService.class );
        exceptionRenderer = mock( ExceptionRenderer.class );
        contentService = mock( ContentService.class );
        projectService = mock( ProjectService.class );

        handler =
            new SlashApiHandler( controllerScriptFactory, apiDescriptorService, contentService, projectService, new ExceptionMapperImpl(),
                                 exceptionRenderer );

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
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );
    }

    @AfterEach
    public void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void testReservedAppKeys()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        List.of( "attachment", "image", "error", "idprovider", "service", "asset", "component", "widgets", "media" ).forEach( appKey -> {
            // as endpoint
            when( webRequest.getEndpointPath() ).thenReturn( "/_/" + appKey + "/path" );
            when( webRequest.getRawPath() ).thenReturn( "/path/_/" + appKey + "/path" );

            WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
            assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
            assertEquals( "Application key [" + appKey + "] is reserved", ex.getMessage() );

            // as API
            when( webRequest.getEndpointPath() ).thenReturn( null );
            when( webRequest.getRawPath() ).thenReturn( "/api/" + appKey + "/apiKey/path" );

            ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
            assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
            assertEquals( "Application key [" + appKey + "] is reserved", ex.getMessage() );
        } );
    }

    @Test
    public void testInvalidPattern()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/path/to/some/resource" );

        // path must start with `/api/` or contains `/_/` as endpoint part
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid API path: /path/to/some/resource", ex.getMessage() );
    }

    @Test
    public void testHttpOptions()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.OPTIONS );

        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp/api-key" );

        WebResponse res = this.handler.handle( webRequest );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );

        when( webRequest.getEndpointPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        res = this.handler.handle( webRequest );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testHandleApiNotFound()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] not found", ex.getMessage() );
    }

    @Test
    public void testHandleApiAccessDenied()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "myapp" ), "myapi" ) )
            .allowedPrincipals( PrincipalKeys.create().add( PrincipalKey.from( "role:principalKey" ) ).build() )
            .mounts( Set.of( ApiMount.API ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "You don't have permission to access \"api-key\" API for \"com.enonic.app.myapp\"", ex.getMessage() );
    }

    @Test
    public void testHandleApi()
        throws Exception
    {
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "myapp" ), "myapi" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.API ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testHandleApiWithoutTracer()
        throws Exception
    {
        Tracer.setManager( null );
        testHandleApi();
    }

    @Test
    public void testHandleApiError()
        throws Exception
    {
        when( this.exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenThrow( new NullPointerException() );

        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "myapp" ), "myapi" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.API ) )
            .contextPath( ApiContextPath.ANY )
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
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    @Test
    public void testAnySiteEndpoint()
        throws Exception
    {
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/site/project/branch/mysite/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_SITES ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        request.setSite( site );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.findNearestSiteByPath( any() ) ).thenReturn( site );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testNoSiteMountForSiteEndpoint()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/site/project/branch/path/to/content/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.WEBAPP ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testSiteEndpointWithProjectEndpoint()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/site/project/branch/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_SITES, ApiMount.SITE ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testSiteEndpointWithInvalidPath()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/some/project/branch/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_SITES, ApiMount.SITE ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testSiteEndpointWithInvalidApplicationName()
    {
        request.setEndpointPath( "/_/ /api-key" );
        request.setRawPath( "/site/project/branch/mysite/_/ /api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Application key [ ] not found", ex.getMessage() );
    }

    @Test
    public void testWebappEndpointWithoutWebappMount()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp2/api-key" );
        request.setRawPath( "/webapp/com.enonic.app.myapp1/path/_/com.enonic.app.myapp2/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp2" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.SITE ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp2:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testWebappEndpointInvalidPath()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/webapp/_/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.WEBAPP ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testAnyWebappEndpointWithSpecificAppWithSubFolder()
        throws Exception
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp2/api-key" );
        request.setRawPath( "/webapp/com.enonic.app.myapp1/subfolder/path/_/com.enonic.app.myapp2/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp2" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_WEBAPPS ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testWebappEndpointWithSpecificAppWithSubFolderWithDefaultContextPath()
        throws Exception
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp2/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp2" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_WEBAPPS ) )
            .contextPath( ApiContextPath.DEFAULT )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        request.setRawPath( "/webapp/com.enonic.app.myapp1/subfolder/_/com.enonic.app.myapp2/api-key" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );

        request.setRawPath( "/webapp/com.enonic.app.myapp1/_/com.enonic.app.myapp2/api-key" );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testWebapp()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp2/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp2" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.WEBAPP ) )
            .contextPath( ApiContextPath.DEFAULT )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        request.setRawPath( "/webapp/com.enonic.app.myapp1/_/com.enonic.app.myapp2/api-key" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testWebappMountIsSkippedIfAnyWebappPresent()
        throws Exception
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp2/api-key" );
        request.setRawPath( "/webapp/com.enonic.app.myapp1/path/_/com.enonic.app.myapp2/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp2" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.WEBAPP, ApiMount.ALL_WEBAPPS ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testAnyContextPathOnAdmin()
        throws Exception
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/tool/path/_/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ADMIN ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testDefaultContextPathOnAdmin()
        throws Exception
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/tool/_/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ADMIN ) )
            .contextPath( ApiContextPath.DEFAULT )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testAdminEndpointInvalidPath()
    {
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/site/path/_/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ADMIN ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] is not mounted", ex.getMessage() );
    }

    @Test
    public void testAdminSiteEndpoint()
        throws Exception
    {
        request.setContentPath( ContentPath.from( "/mysite/contentPath" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/admin/site/project/branch/mysite/contentPath/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.ALL_SITES ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.findNearestSiteByPath( any() ) ).thenReturn( site );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }


    @Test
    public void testDefaultContextPathOnSite()
        throws Exception
    {
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/site/project/branch/mysite/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        final SiteConfigs siteConfigs = mock( SiteConfigs.class );
        when( siteConfigs.get( ApplicationKey.from( "com.enonic.app.myapp" ) ) ).thenReturn(
            SiteConfig.create().application( ApplicationKey.from( "com.enonic.app.myapp" ) ).config( new PropertyTree() ).build() );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );

        when( contentService.getByPath( any() ) ).thenReturn( site );
        when( contentService.findNearestSiteByPath( any() ) ).thenReturn( site );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.SITE ) )
            .contextPath( ApiContextPath.DEFAULT )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testSiteEndpointWithFallbackToProject()
        throws Exception
    {
        request.setContentPath( ContentPath.from( "/mysite" ) );
        request.setEndpointPath( "/_/com.enonic.app.myapp/api-key" );
        request.setRawPath( "/site/project/branch/mysite/_/com.enonic.app.myapp/api-key" );
        request.setRepositoryId( RepositoryId.from( "com.enonic.cms.project" ) );
        request.setBranch( Branch.from( "branch" ) );

        final SiteConfigs siteConfigs = mock( SiteConfigs.class );
        when( siteConfigs.get( ApplicationKey.from( "com.enonic.app.myapp" ) ) ).thenReturn( null );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );

        when( contentService.getByPath( any() ) ).thenReturn( site );
        when( contentService.findNearestSiteByPath( any() ) ).thenReturn( site );

        final SiteConfigs projectConfigs = mock( SiteConfigs.class );
        when( projectConfigs.get( ApplicationKey.from( "com.enonic.app.myapp" ) ) ).thenReturn(
            SiteConfig.create().application( ApplicationKey.from( "com.enonic.app.myapp" ) ).config( new PropertyTree() ).build() );

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( projectConfigs );
        when( projectService.get( any() ) ).thenReturn( project );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api-key" ) )
            .allowedPrincipals( null )
            .mounts( Set.of( ApiMount.SITE ) )
            .contextPath( ApiContextPath.ANY )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );

        when( project.getSiteConfigs() ).thenReturn( mock( SiteConfigs.class ) );
        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

}
