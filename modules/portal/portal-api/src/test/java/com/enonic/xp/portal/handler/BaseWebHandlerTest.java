package com.enonic.xp.portal.handler;

import org.junit.Before;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebHandler;

import static org.junit.Assert.*;

public abstract class BaseWebHandlerTest
{
    protected PortalWebRequest request;

    protected PortalWebResponse response = new PortalWebResponse();

    @Before
    public final void setup()
        throws Exception
    {
        final PortalWebRequest.Builder requestBuilder = PortalWebRequest.create().mode( RenderMode.LIVE );
        configure( requestBuilder );
        request = requestBuilder.build();
    }

    protected abstract void configure( final PortalWebRequest.Builder requestBuilder )
        throws Exception;

    protected final void assertMethodNotAllowed( final WebHandler handler, final HttpMethod method )
        throws Exception
    {
        try
        {
            final PortalWebRequest portalWebRequest = PortalWebRequest.create( this.request ).method( method ).build();
            handler.handle( portalWebRequest, this.response, null );
        }
        catch ( final WebException e )
        {
            assertEquals( "Method " + method + " should not be allowed", e.getStatus(), HttpStatus.METHOD_NOT_ALLOWED );
            return;
        }

        fail( "Method " + method + " should not be allowed" );
    }

    protected void setEndpointPath( String endpointPath )
    {
        this.request = PortalWebRequest.create( this.request ).endpointPath( endpointPath ).build();
    }

    protected void setMethod( HttpMethod method )
    {
        this.request = PortalWebRequest.create( this.request ).method( method ).build();
    }

    protected void setMode( RenderMode mode )
    {
        this.request = PortalWebRequest.create( this.request ).mode( mode ).build();
    }

    protected void setContentPath( ContentPath contentPath )
    {
        this.request = PortalWebRequest.create( this.request ).contentPath( contentPath ).build();
    }
}
