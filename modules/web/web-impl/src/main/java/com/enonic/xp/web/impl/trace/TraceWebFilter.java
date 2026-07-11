package com.enonic.xp.web.impl.trace;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.trace.Trace;
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
            trace.put( "path", req.getPath() );
            trace.put( "rawpath", req.getRawPath() );
            trace.put( "url", req.getUrl() );
            trace.put( "method", req.getMethod().toString() );
            trace.put( "host", req.getHost() );
            addContextInfo( trace );
        } );

        final WebResponse webResponse = chain.handle( req, res );

        Tracer.withCurrent( trace -> addTraceInfo( trace, webResponse ) );

        return webResponse;
    }

    private static void addContextInfo( final Trace trace )
    {
        final Context context = ContextAccessor.current();
        trace.put( "repo", Objects.toString( context.getRepositoryId(), null ) );
        trace.put( "branch", Objects.toString( context.getBranch(), null ) );
        final AuthenticationInfo authInfo = context.getAuthInfo();
        if ( authInfo != null && authInfo.getUser() != null )
        {
            trace.put( "user", authInfo.getUser().getKey().toString() );
        }
    }
}
