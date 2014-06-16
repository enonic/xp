package com.enonic.wem.portal.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.portal.controller.JsContext;

import static org.junit.Assert.*;

public class ContentHandlerTest
    extends RenderBaseHandlerTest<ContentHandler>
{
    @Override
    protected ContentHandler createResource()
    {
        return new ContentHandler();
    }

    @Before
    public void setup()
        throws Exception
    {
        super.setup();
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final ClientResponse response = executeGet( "/live/site/somepath/content" );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        Mockito.verify( this.jsController ).context( jsContext.capture() );
        Mockito.verify( this.jsController ).execute();

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getType().toString() );
    }

    @Test
    public void getContentNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject(), Mockito.anyObject() ) ).thenReturn( null );

        final ClientResponse response = executeGet( "/live/site/somepath/content" );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getContentWithTemplateNotFound()
        throws Exception
    {
        setupContentAndSite();

        final ClientResponse response = executeGet( "/live/site/somepath/content" );
        assertEquals( 404, response.getStatus() );
    }
}
