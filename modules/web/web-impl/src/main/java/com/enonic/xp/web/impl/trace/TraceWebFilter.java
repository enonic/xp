package com.enonic.xp.web.impl.trace;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.common.primitives.Longs;

import com.enonic.xp.resource.Resource;
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

        Tracer.withCurrent( trace -> {
            trace.attribute( "status", webResponse.getStatus().value() );
            trace.attribute( "type", webResponse.getContentType().toString() );
            final Long size = responseSize( webResponse );
            if ( size != null )
            {
                trace.attribute( "size", size );
            }
        } );

        return webResponse;
    }

    static @Nullable Long responseSize( final WebResponse webResponse )
    {
        final String length = webResponse.getHeaders().get( HttpHeaders.CONTENT_LENGTH );
        if ( length != null )
        {
            return Longs.tryParse( length );
        }

        try
        {
            return bodySize( webResponse.getBody(), webResponse.getContentType() );
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    static @Nullable Long bodySize( final @Nullable Object body, final MediaType contentType )
        throws IOException
    {
        return switch ( body )
        {
            case null -> 0L;
            case Resource resource -> knownSize( resource.getSize() );
            case ByteSource byteSource -> byteSource.size();
            case byte[] bytes -> (long) bytes.length;
            // serialized as JSON only when the response is written
            case Map<?, ?> _, List<?> _ -> null;
            default -> textSize( body.toString(), contentType );
        };
    }

    private static @Nullable Long knownSize( final long size )
    {
        return size >= 0 ? size : null;
    }

    private static @Nullable Long textSize( final String text, final MediaType contentType )
    {
        final Charset charset = contentType.charset().orNull();
        if ( charset == null )
        {
            return null;
        }
        // same replacement of malformed input as the response serializer
        return (long) text.getBytes( charset ).length;
    }
}
