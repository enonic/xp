package com.enonic.xp.portal.impl.exception;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.when;

public class ExceptionRendererImplTest
{
    private ExceptionRendererImpl renderer;

    private PortalRequest request;

    private ResourceService resourceService;

    private ContentService contentService;

    private ErrorHandlerScriptFactory errorHandlerScriptFactory;

    @Before
    public void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        this.contentService = Mockito.mock( ContentService.class );
        this.errorHandlerScriptFactory = Mockito.mock( ErrorHandlerScriptFactory.class );

        this.renderer = new ExceptionRendererImpl();
        this.renderer.setResourceService( resourceService );
        this.renderer.setContentService( contentService );
        this.renderer.setErrorHandlerScriptFactory( errorHandlerScriptFactory );
        this.request = new PortalRequest();
    }

    @Test
    public void render_json()
    {
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertEquals( HttpStatus.NOT_FOUND, res.getStatus() );
        assertEquals( MediaType.JSON_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertEquals( "{\"status\":404,\"message\":\"Custom message\"}", body );
    }

    @Test
    public void render_html()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertEquals( HttpStatus.NOT_FOUND, res.getStatus() );
        assertEquals( MediaType.HTML_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertTrue( body.contains( "404 Not Found" ) );
        assertTrue( body.contains( "Custom message" ) );

        // Should not show exception
        assertTrue( body.contains( ExceptionRendererImplTest.class.getName() ) );
    }

    @Test
    public void render_json_withCause()
    {
        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );
        assertEquals( HttpStatus.BAD_REQUEST, res.getStatus() );
        assertEquals( MediaType.JSON_UTF_8.withoutParameters(), res.getContentType() );
        MediaType.create( "", "" );

        final String body = res.getBody().toString();
        assertEquals( "{\"status\":400,\"message\":\"Custom message (java.lang.RuntimeException)\"}", body );
    }

    @Test
    public void render_html_withCause()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );
        assertEquals( HttpStatus.BAD_REQUEST, res.getStatus() );
        assertEquals( MediaType.HTML_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertTrue( body.contains( "400 Bad Request" ) );
        assertTrue( body.contains( "Custom message" ) );

        // Should show exception
        assertTrue( body.contains( RuntimeException.class.getName() ) );
    }

    @Test
    public void render_custom_error_with_site_context()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        final Site site = newSite();
        this.request.setSite( site );
        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "site/error/error.js" );
        final ErrorHandlerScript errorHandlerScript =
            ( portalError ) -> PortalResponse.create().body( "Custom message page" ).status( HttpStatus.BAD_REQUEST ).build();

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = Mockito.mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );

        assertEquals( HttpStatus.BAD_REQUEST, res.getStatus() );
        assertEquals( "Custom message page", res.getBody().toString() );
    }

    @Test
    public void render_custom_error_for_404_in_site_path()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        this.request.setContentPath( ContentPath.from( "/mysite/some/long/path" ) );

        final Site site = newSite();
        when( contentService.getByPath( ContentPath.from( "/mysite" ) ) ).thenReturn( site );
        when( contentService.getByPath( not( Matchers.eq( ContentPath.from( "/mysite" ) ) ) ) ).thenThrow(
            new ContentNotFoundException( ContentPath.from( "/" ), Branch.from( "draft" ) ) );

        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "site/error/error.js" );
        final ErrorHandlerScript errorHandlerScript =
            ( portalError ) -> PortalResponse.create().body( "Custom message page" ).status( HttpStatus.NOT_FOUND ).build();

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = Mockito.mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.NOT_FOUND, cause ) );

        assertEquals( HttpStatus.NOT_FOUND, res.getStatus() );
        assertEquals( "Custom message page", res.getBody().toString() );
    }

    @Test
    public void render_default_error_page_when_error_in_custom_handler()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );
        final Site site = newSite();
        this.request.setSite( site );
        final ResourceKey errorResource = ResourceKey.from( ApplicationKey.from( "myapplication" ), "site/error/error.js" );
        final ErrorHandlerScript errorHandlerScript = ( portalError ) -> {
            throw new RuntimeException( "Something went wrong in the handler script" );
        };

        when( this.errorHandlerScriptFactory.errorScript( errorResource ) ).thenReturn( errorHandlerScript );
        final Resource resource = Mockito.mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( errorResource ) ).thenReturn( resource );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );

        final String body = res.getBody().toString();
        assertTrue( body.contains( "400 Bad Request" ) );
        assertTrue( body.contains( "Custom message" ) );
    }

    private Site newSite()
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "Field", 42l );

        final SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( siteConfigConfig ).
            build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "100123" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

}
