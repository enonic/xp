package com.enonic.xp.portal.impl.handler;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

@Component(service = ErrorHandler.class)
public class ErrorHandler
{
    public PortalResponse handle( final WebRequest webRequest )
        throws Exception
    {
        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final String restPath = HandlerHelper.findRestPath( webRequest, "error" );

        HttpStatus code = parseStatus( restPath );

        if ( code == null )
        {
            code = HttpStatus.NOT_FOUND;
        }

        String message = HandlerHelper.getParameter( webRequest, "message" );

        if ( message == null )
        {
            message = code.getReasonPhrase();
        }

        throw new WebException( code, message, false );
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
}
