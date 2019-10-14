package com.enonic.xp.web.handler;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseHandlerTest
{
    protected final void assertMethodNotAllowed( final WebHandler handler, final HttpMethod method, final WebRequest request )
        throws Exception
    {
        try
        {
            request.setMethod( method );
            handler.handle( request, WebResponse.create().build(), null );
        }
        catch ( final WebException e )
        {
            assertEquals( e.getStatus(), HttpStatus.METHOD_NOT_ALLOWED, "Method " + method + " should not be allowed" );
            return;
        }

        fail( "Method " + method + " should not be allowed" );
    }
}
