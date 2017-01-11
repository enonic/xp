package com.enonic.xp.web.impl.trace;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;
import com.google.common.primitives.Longs;

import com.enonic.xp.resource.Resource;
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
        return req.getPath().startsWith( "/portal/" ) || req.getPath().startsWith( "/admin/portal/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Trace trace = Tracer.newTrace( "portalRequest" );
        if ( trace != null )
        {
            trace.put( "path", req.getPath() );
            trace.put( "method", req.getMethod().toString() );
            trace.put( "host", req.getHost() );
        }

        return Tracer.traceEx( trace, () ->
        {
            final WebResponse webResponse = chain.handle( req, res );
            if ( trace != null )
            {
                trace.put( "status", webResponse.getStatus().value() );
                trace.put( "type", webResponse.getContentType().toString() );
                trace.put( "size", getSize( webResponse ) );
            }
            return webResponse;
        } );
    }

    private Long getSize( final WebResponse webResponse )
    {
        final String length = webResponse.getHeaders().get( "Content-Length" );
        if ( length != null )
        {
            return Longs.tryParse( length );
        }
        else
        {
            try
            {
                return getBodyLength( webResponse.getBody() );
            }
            catch ( IOException e )
            {
                return null;
            }
        }
    }

    private Long getBodyLength( final Object body )
        throws IOException
    {
        if ( body instanceof Resource )
        {
            return ( (Resource) body ).getSize();
        }

        if ( body instanceof ByteSource )
        {
            return ( (ByteSource) body ).size();
        }

        if ( body instanceof Map )
        {
            return null; // TODO
        }

        if ( body instanceof byte[] )
        {
            return (long) ( (byte[]) body ).length;
        }

        if ( body != null )
        {
            return (long) body.toString().length();
        }
        return 0L;
    }
}
