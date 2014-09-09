package com.enonic.wem.portal.internal.content;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

import com.enonic.wem.portal.internal.controller.JsContext;

import static org.junit.Assert.*;

public class ContentResourceTest
    extends RenderBaseResourceTest<ContentResource>
{


    @Override
    protected void configure()
        throws Exception
    {
        this.resource = new ContentResource();
        super.configure();
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite( testContext );
        setupTemplates();

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content" );
        final Response response = executeRequest( request );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        Mockito.verify( this.jsController ).execute( jsContext.capture() );

        assertEquals( 200, response.getStatus().getCode() );
        assertEquals( "text/plain", response.getEntity().getMediaType().toString() );
        assertEquals( "site/somepath/content", this.resource.contentPath );
    }

    @Test
    public void getContentNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject(), Mockito.anyObject() ) ).thenReturn( null );

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
    }

    @Test
    public void getContentWithTemplateNotFound()
        throws Exception
    {
        setupContentAndSite( testContext );

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
    }
}
