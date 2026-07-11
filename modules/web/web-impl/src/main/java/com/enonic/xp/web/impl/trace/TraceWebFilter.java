package com.enonic.xp.web.impl.trace;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.trace.Traced;
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
        return req.getBasePath().startsWith( "/site/" ) || req.getBasePath().startsWith( "/webapp/" ) ||
            req.getBasePath().startsWith( "/admin/" ) || req.getBasePath().startsWith( "/api/" );
    }

    @Override
    @Traced("portalRequest")
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "path", req.getPath() );
            trace.attribute( "rawpath", req.getRawPath() );
            trace.attribute( "url", req.getUrl() );
            trace.attribute( "method", req.getMethod().toString() );
            trace.attribute( "host", req.getHost() );
        } );

        final WebResponse webResponse = chain.handle( req, res );

        Tracer.withCurrent( trace -> addTraceInfo( trace, webResponse ) );

        return webResponse;
    }
}
