package com.enonic.xp.portal.impl.exception;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionRendererImplTest
{
    private ExceptionRendererImpl renderer;

    private PortalRequest request;

    private ResourceService resourceService;

    private IdProviderControllerService idProviderControllerService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private MockPostProcessor postProcessor;

    @BeforeEach
    void setup()
    {
        this.resourceService = mock( ResourceService.class );
        this.idProviderControllerService = mock( IdProviderControllerService.class );
        this.errorHandlerScriptFactory = mock( ErrorHandlerScriptFactory.class );
        this.postProcessor = new MockPostProcessor();
        RunModeSupport.set( RunMode.DEV );
        this.renderer =
            new ExceptionRendererImpl( resourceService, errorHandlerScriptFactory, null, postProcessor, new ExceptionMapperImpl() );
        this.request = new PortalRequest();
        this.request.setMode( RenderMode.LIVE );

        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        this.request.setRawRequest( rawRequest );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
    }

    @Test
    void render_json()
    {
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.NOT_FOUND );
        assertThat( res.getContentType() ).isEqualTo( MediaType.JSON_UTF_8 );
        assertThat( res.getBody().toString() ).isEqualTo( "{\"status\":404,\"message\":\"Custom message\"}" );
    }

    @Test
    void render_html()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.NOT_FOUND );
        assertThat( res.getContentType() ).isEqualTo( MediaType.HTML_UTF_8 );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "404 Not Found" );
        assertThat( body ).contains( "Custom message" );

        // Should show exception info in dev mode
        assertThat( body ).contains( ExceptionRendererImplTest.class.getName() );
    }

    @Test
    void render_with_tip()
    {
        RunModeSupport.set( RunMode.PROD );
        this.renderer =
            new ExceptionRendererImpl( resourceService, errorHandlerScriptFactory, null, postProcessor, new ExceptionMapperImpl() );

        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        this.request.setBaseUri( "/site" );
        this.request.setBranch( Branch.from( "master" ) );

        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.NOT_FOUND, "Custom message" ) );

        assertThat( res.getStatus() ).isEqualTo( HttpStatus.NOT_FOUND );
        assertThat( res.getContentType() ).isEqualTo( MediaType.HTML_UTF_8 );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "404 - Not Found" );
        assertThat( body ).doesNotContain( "Custom message" );
        assertThat( body ).contains( "Tip: Did you remember to publish the site?" );
    }

    @Test
    void render_json_withCause()
    {
        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.BAD_REQUEST, cause ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( res.getContentType() ).isEqualTo( MediaType.JSON_UTF_8 );
        assertThat( res.getBody().toString() ).isEqualTo( "{\"status\":400,\"message\":\"Custom message (java.lang.RuntimeException)\"}" );
    }

    @Test
    void render_html_withCause()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.BAD_REQUEST, cause ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( res.getContentType() ).isEqualTo( MediaType.HTML_UTF_8 );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "400 Bad Request" );
        assertThat( body ).contains( "Custom message" );

        // Should show exception
        assertThat( body ).contains( RuntimeException.class.getName() );
    }

    @Test
    void render_custom_error_with_site_context()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        final Site site = newSite();
        this.request.setBaseUri( "/site" );
        this.request.setSite( site );
        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "cms/error/error.js" );
        final ErrorHandlerScript errorHandlerScript = ( portalError, handlerMethod ) -> PortalResponse.create()
            .body( "Custom message page" )
            .status( HttpStatus.BAD_REQUEST )
            .postProcess( false )
            .build();

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.BAD_REQUEST, cause ) );

        assertThat( res.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( res.getBody().toString() ).isEqualTo( "Custom message page" );
        assertThat( postProcessor.isExecuted() ).isFalse();
    }

    @Test
    void render_custom_error_for_404_in_site_path()
    {
        this.request.setBaseUri( "/site" );
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        this.request.setContentPath( ContentPath.from( "/site/myproject/draft/mysite/some/long/path" ) );

        final Site site = newSite();
        this.request.setSite( site );

        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "cms/error/error.js" );
        final ErrorHandlerScript errorHandlerScript = ( portalError, handleMethod ) -> PortalResponse.create()
            .body( "Custom message page" )
            .status( HttpStatus.NOT_FOUND )
            .postProcess( false )
            .build();

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.NOT_FOUND, cause ) );

        assertThat( res.getStatus() ).isEqualTo( HttpStatus.NOT_FOUND );
        assertThat( res.getBody().toString() ).isEqualTo( "Custom message page" );
        assertThat( postProcessor.isExecuted() ).isFalse();
    }

    @Test
    void render_default_error_page_when_error_in_custom_handler()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        final Site site = newSite();
        this.request.setSite( site );
        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "cms/error/error.js" );
        final ErrorHandlerScript errorHandlerScript = ( portalError, handleMethod ) -> {
            throw new RuntimeException( "Something went wrong in the handler script" );
        };

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.BAD_REQUEST, cause ) );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "400 Bad Request" );
        assertThat( body ).contains( "Custom message" );
    }

    @Test
    void customErrorWithPostProcessing()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        final Site site = newSite();
        this.request.setBaseUri( "/site" );
        this.request.setSite( site );
        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "cms/error/error.js" );
        final ErrorHandlerScript errorHandlerScript = ( portalError, handlerMethod ) -> PortalResponse.create()
            .body( "<h1>Custom message page</h1><!--#COMPONENT module:myPart -->" )
            .status( HttpStatus.BAD_REQUEST )
            .postProcess( true )
            .build();

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.BAD_REQUEST, cause ) );

        assertThat( res.getStatus() ).isEqualTo( HttpStatus.BAD_REQUEST );
        assertThat( res.getBody().toString() ).isEqualTo( "<h1>Custom message page</h1><h3>My Part</h3>" );
        assertThat( postProcessor.isExecuted() ).isTrue();
    }

    @Test
    void testRender_RenderMode_Admin()
    {
        this.request.setMode( RenderMode.ADMIN );
        this.request.setApplicationKey( ApplicationKey.from( "myapplication-key" ) );

        final PortalResponse result = renderer.render( this.request, new WebException( HttpStatus.NOT_FOUND, "Resource not found" ) );

        assertThat( result ).isNotNull();
        assertThat( result.getBody().toString() ).isEqualTo( "{\"status\":404,\"message\":\"Resource not found\"}" );
    }

    @Test
    void render_internal_server_error()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final PortalResponse res =
            this.renderer.render( this.request, new WebException( HttpStatus.INTERNAL_SERVER_ERROR, "Custom message" ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.INTERNAL_SERVER_ERROR );
        assertThat( res.getContentType() ).isEqualTo( MediaType.HTML_UTF_8 );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "500 Internal Server Error" );
        assertThat( body ).contains( "Custom message" );

        // Should show exception info in dev mode
        assertThat( body ).contains( ExceptionRendererImplTest.class.getName() );
    }

    @Test
    void testRenderForbidden()
        throws IOException
    {
        RunModeSupport.set( RunMode.PROD );
        this.renderer = new ExceptionRendererImpl( resourceService, errorHandlerScriptFactory, idProviderControllerService, postProcessor,
                                                   new ExceptionMapperImpl() );

        when( idProviderControllerService.execute( any( IdProviderControllerExecutionParams.class ) ) ).thenReturn( null );
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/admin" );
        when( virtualHost.getTarget() ).thenReturn( "/admin" );
        when( request.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( request.getRawRequest(), virtualHost );

        final RuntimeException cause = new RuntimeException( "Custom message" );

        final PortalResponse res = this.renderer.render( this.request, new WebException( HttpStatus.FORBIDDEN, cause ) );
        assertThat( res.getStatus() ).isEqualTo( HttpStatus.FORBIDDEN );
        assertThat( res.getContentType() ).isEqualTo( MediaType.HTML_UTF_8 );

        final String body = res.getBody().toString();
        assertThat( body ).contains( "<h3>403 - Forbidden</h3>" );

        // test with already authenticated user
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().user( User.anonymous() ).build();
        final Context context = ContextBuilder.from( ContextAccessor.current() ).authInfo( authenticationInfo ).build();

        final PortalResponse response =
            context.callWith( () -> this.renderer.render( this.request, new WebException( HttpStatus.FORBIDDEN, cause ) ) );

        assertThat( response.getStatus() ).isEqualTo( HttpStatus.FORBIDDEN );

        final String responseBody = response.getBody().toString();
        assertThat( responseBody ).contains( "<h3>403 - Forbidden</h3>" );
        assertThat( responseBody ).contains( "class=\"logout\"" ).contains( "/_/idprovider/system/logout" );
    }

    private Site newSite()
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "Field", 42L );

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( siteConfigConfig ).build();

        final PropertyTree siteData = new PropertyTree();
        PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "100123" ) );
        site.data( siteData );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }
}