package com.enonic.xp.web.impl.trace;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

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
        return req.getRawPath().startsWith( "/portal/" ) || req.getRawPath().startsWith( "/app/" ) ||
            req.getRawPath().startsWith( "/admin/portal/" ) || req.getRawPath().startsWith( "/admin/tool/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Trace trace = Tracer.newTrace( "portalRequest" );
        if ( trace == null )
        {
            return chain.handle( req, res );
        }

        trace.put( "path", req.getPath() );
        trace.put( "rawpath", req.getRawPath() );
        trace.put( "url", ServletRequestUrlHelper.getFullUrl( req.getRawRequest() ) );
        trace.put( "method", req.getMethod().toString() );
        trace.put( "host", req.getHost() );
        trace.put( "httpRequest", req );
        trace.put( "httpResponse", res );
        trace.put( "context", ContextAccessor.current() );

        return Tracer.traceEx( trace, () -> {
            final WebResponse webResponse = chain.handle( req, res );
            addTraceInfo( trace, webResponse );
            trace.put( "httpResponse", webResponse );
            return webResponse;
        } );
    }
}
