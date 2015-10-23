package com.enonic.xp.portal.impl.handler.error;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.web.HttpStatus;

@Component(immediate = true, service = PortalHandler.class)
public final class ErrorHandler
    extends EndpointHandler
{
    public ErrorHandler()
    {
        super( "error" );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );

        final ErrorHandlerWorker worker = new ErrorHandlerWorker();
        worker.code = parseStatus( restPath );
        worker.message = getParameter( req, "message" );

        return worker;
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

    private String getParameter( final PortalRequest req, final String name )
    {
        final Collection<String> values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }
}
