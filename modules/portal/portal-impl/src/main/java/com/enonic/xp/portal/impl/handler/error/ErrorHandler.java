package com.enonic.xp.portal.impl.handler.error;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ErrorHandler
    extends EndpointHandler
{
    public ErrorHandler()
    {
        super( "error" );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            // it will be handled by default OPTIONS handler in BaseWebHandler
            return PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        final String restPath = findRestPath( webRequest );

        final ErrorHandlerWorker worker = new ErrorHandlerWorker( webRequest );
        worker.code = parseStatus( restPath );
        worker.message = getParameter( webRequest, "message" );

        return worker.execute();
    }

    private HttpStatus parseStatus( final String value )
    {
        try
        {
            return HttpStatus.from( Integer.parseInt( value ) );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private String getParameter( final WebRequest req, final String name )
    {
        final Collection<String> values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }
}
