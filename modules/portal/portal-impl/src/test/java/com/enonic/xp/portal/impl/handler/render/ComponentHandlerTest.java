package com.enonic.xp.portal.impl.handler.render;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebResponse;

import static org.junit.Assert.*;

public class ComponentHandlerTest
    extends RenderBaseHandlerTest
{
    private ComponentHandler handler;


    @Override
    protected void configure( final PortalWebRequest.Builder requestBuilder )
        throws Exception
    {
        super.configure( requestBuilder );
        this.handler = new ComponentHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setPageDescriptorService( this.pageDescriptorService );
        this.handler.setPageTemplateService( this.pageTemplateService );
        this.handler.setRendererFactory( this.rendererFactory );
        this.handler.setPostProcessor( this.postProcessor );

        requestBuilder.method( HttpMethod.GET );
        requestBuilder.contentPath( ContentPath.from( "/site/somepath/content" ) );
        requestBuilder.endpointPath( "/_/component/main" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/_/other/main/1" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/component/main/1" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        setEndpointPath( "/_/component/main/1" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, this.response, null );
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

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        setEndpointPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request, this.response, null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "component rendered", res.getBody() );
    }

    @Test
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent();

        setEndpointPath( "/_/component/main-region/0" );

        try
        {
            this.handler.handle( this.request, this.response, null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
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

        setEndpointPath( "/_/component/main-region/666" );

        try
        {
            this.handler.handle( this.request, this.response, null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Page component for [main-region/666] not found", e.getMessage() );
        }
    }
}
