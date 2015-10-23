package com.enonic.xp.portal.handler;

import org.junit.Before;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public abstract class BaseHandlerTest
{
    protected PortalRequest request;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.request.setMode( RenderMode.LIVE );
        configure();
    }

    protected abstract void configure()
        throws Exception;

    protected final void assertMethodNotAllowed( final PortalHandler handler, final HttpMethod method )
        throws Exception
    {
        try
        {
            this.request.setMethod( method );
            handler.handle( this.request );
        }
        catch ( final PortalException e )
        {
            assertEquals( "Method " + method + " should not be allowed", e.getStatus(), HttpStatus.METHOD_NOT_ALLOWED );
            return;
        }

        fail( "Method " + method + " should not be allowed" );
    }
}
