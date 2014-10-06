package com.enonic.wem.portal.internal.content;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.wem.portal.internal.controller.JsContext;

import static org.junit.Assert.*;

public class ContentResourceTest
    extends RenderBaseResourceTest<ContentResourceProvider>
{
    @Override
    protected void configure()
        throws Exception
    {
        this.resourceProvider = new ContentResourceProvider();
        super.configure();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        Mockito.verify( this.jsController ).execute( jsContext.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getContentType() );
    }

    @Test
    public void getContentNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getContentWithTemplateNotFound()
        throws Exception
    {
        setupContentAndSite();

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void renderForNoPageDescriptor()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates( false );

        final MockHttpServletRequest request = newGetRequest( "/edit/test/id" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        Mockito.verify( this.postProcessor ).processResponse( Mockito.anyObject() );
    }
}
