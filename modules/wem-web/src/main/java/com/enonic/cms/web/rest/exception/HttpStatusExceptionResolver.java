package com.enonic.cms.web.rest.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.api.util.LogFacade;

public final class HttpStatusExceptionResolver
    implements HandlerExceptionResolver
{
    private final static LogFacade LOG = LogFacade.get( HttpStatusExceptionResolver.class );

    public ModelAndView resolveException( final HttpServletRequest req, final HttpServletResponse res, final Object handler,
                                          final Exception ex )
    {
        if ( !( ex instanceof HttpStatusException ) )
        {
            return null;
        }
        
        try {
            handleStatusException( res, ((HttpStatusException)ex) );
        } catch (final Exception e) {
            LOG.errorCause( "Failed to render status exception {0}", e, ex.getClass().getName() );
        }

        return new ModelAndView();
    }

    private void handleStatusException( final HttpServletResponse res, final HttpStatusException e )
        throws Exception
    {
        for (final Map.Entry<String, String> entry : e.getHeaders().toSingleValueMap().entrySet()) {
            res.setHeader( entry.getKey(), entry.getValue() );
        }

        final HttpStatus status = e.getStatus();
        res.sendError( status.value(), status.getReasonPhrase() );
    }
}
