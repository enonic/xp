package com.enonic.xp.portal.impl.handler.render;

import org.junit.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ComponentHandlerTest
    extends RenderBaseHandlerTest
{
    private ComponentHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
        super.configure();

        this.handler = new ComponentHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setPageDescriptorService( this.pageDescriptorService );
        this.handler.setPageTemplateService( this.pageTemplateService );
        this.handler.setRendererFactory( this.rendererFactory );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( "/_/component/main" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/main/1" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/component/main/1" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/component/main/1" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( HttpMethod.OPTIONS );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testComponentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().
            body( "component rendered" ).
            header( "some-header", "some-value" ).
            status( HttpStatus.OK ).
            build();

        setRendererResult( portalResponse );

        this.request.setEndpointPath( "/_/component/main-region/0" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "text/plain; charset=utf-8", res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "component rendered", res.getBody() );
    }

    @Test
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent();

        this.request.setEndpointPath( "/_/component/main-region/0" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "No template found for content", e.getMessage() );
        }
    }

    @Test
    public void getComponentNotFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        this.request.setEndpointPath( "/_/component/main-region/666" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Page component for [main-region/666] not found", e.getMessage() );
        }
    }
}
