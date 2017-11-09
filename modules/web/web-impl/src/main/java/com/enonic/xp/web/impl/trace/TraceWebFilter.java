package com.enonic.xp.web.impl.trace;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class TraceWebFilter
    extends BaseWebHandler
{
    public TraceWebFilter()
    {
        super( -100 );
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        return req.getRawPath().startsWith( "/portal/" ) || req.getRawPath().startsWith( "/admin/portal/" ) ||
            req.getRawPath().startsWith( "/app/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Trace trace = Tracer.newTrace( "portalRequest" );
        if ( trace != null )
        {
            trace.put( "path", req.getPath() );
            trace.put( "rawpath", req.getRawPath() );
            trace.put( "method", req.getMethod().toString() );
            trace.put( "host", req.getHost() );
        }

        return Tracer.traceEx( trace, () ->
        {
            final WebResponse webResponse = chain.handle( req, res );
            addTraceInfo( trace, webResponse );
            return webResponse;
        } );
    }
}
