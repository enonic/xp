package com.enonic.xp.portal.impl.handler.error;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.EndpointWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class ErrorWebHandler
    extends EndpointWebHandler
{
    public ErrorWebHandler()
    {
        super( -50, "error" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String endpointSubPath = getEndpointSubPath( webRequest );

        final PortalWebRequest portalWebRequest = PortalWebRequest.create( webRequest ).build();
        return ErrorWebHandlerWorker.create().
            portalWebRequest( portalWebRequest ).
            portalWebResponse( new PortalWebResponse() ).
            code( parseStatus( endpointSubPath ) ).
            message( getParameter( webRequest, "message" ) ).
            build().
            execute();
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
