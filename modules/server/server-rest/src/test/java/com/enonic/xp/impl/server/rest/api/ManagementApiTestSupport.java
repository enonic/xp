package com.enonic.xp.impl.server.rest.api;

import java.util.Map;
import java.util.concurrent.Callable;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.dispatch.DispatchConstants;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ManagementApiTestSupport
{
    private ManagementApiTestSupport()
    {
    }

    /**
     * A request as it arrives on the management connector: the API path is the raw path, {@code /server:app/...}.
     */
    static WebRequest request( final HttpMethod method, final String rawPath )
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        when( rawRequest.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );

        final WebRequest request = new WebRequest();
        request.setMethod( method );
        request.setRawPath( rawPath );
        request.setRawRequest( rawRequest );
        return request;
    }

    static WebRequest request( final HttpMethod method, final String rawPath, final String body )
    {
        final WebRequest request = request( method, rawPath );
        request.setBody( body );
        return request;
    }

    /**
     * Runs in a context carrying vhost context attributes, the way {@code ContextFilter} sets them up.
     */
    static <T> T withVirtualHostContext( final Map<String, String> attributes, final Callable<T> callable )
    {
        final Context context = ContextBuilder.create().build();
        attributes.forEach( ( key, value ) -> context.getLocalScope().setAttribute( key, value ) );
        return context.callWith( callable );
    }
}
