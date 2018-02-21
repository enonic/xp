package com.enonic.xp.web.handler;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.io.ByteSource;
import com.google.common.primitives.Longs;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Beta
public abstract class BaseWebHandler
    implements WebHandler
{
    private static final int DEFAULT_ORDER = 0;

    private int order;

    private EnumSet<HttpMethod> methodsAllowed;

    public BaseWebHandler()
    {
        this( DEFAULT_ORDER );
    }

    public BaseWebHandler( final int order )
    {
        this( order, HttpMethod.standard() );
    }

    public BaseWebHandler( final EnumSet<HttpMethod> methodsAllowed )
    {
        this( DEFAULT_ORDER, methodsAllowed );
    }

    public BaseWebHandler( final int order, final EnumSet<HttpMethod> methodsAllowed )
    {
        this.order = order;
        this.methodsAllowed = methodsAllowed;
    }

    @Override
    public int getOrder()
    {
        return order;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        if ( canHandle( webRequest ) )
        {
            final HttpMethod method = webRequest.getMethod();
            checkMethodAllowed( method );

            final WebResponse response = doHandle( webRequest, webResponse, webHandlerChain );
            if ( HttpMethod.OPTIONS == method && response.getStatus() == HttpStatus.METHOD_NOT_ALLOWED )
            {
                return handleDefaultOptions();
            }
            return response;
        }
        else
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

    }

    protected abstract boolean canHandle( final WebRequest webRequest );

    protected abstract WebResponse doHandle( WebRequest webRequest, WebResponse webResponse, WebHandlerChain webHandlerChain )
        throws Exception;

    private void checkMethodAllowed( final HttpMethod method )
    {
        if ( !methodsAllowed.contains( method ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
        }
    }

    private WebResponse handleDefaultOptions()
    {
        return WebResponse.create().
            status( HttpStatus.OK ).
            header( "Allow", Joiner.on( "," ).join( this.methodsAllowed ) ).
            build();
    }

    protected final WebException badRequest( final String message, final Object... args )
    {
        return WebException.badRequest( String.format( message, args ) );
    }

    protected final WebException notFound( final String message, final Object... args )
    {
        return new WebException( HttpStatus.NOT_FOUND, String.format( message, args ) );
    }

    protected final WebException methodNotAllowed( final String message, final Object... args )
    {
        return new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( message, args ) );
    }

    protected Long getSize( final WebResponse webResponse )
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

    protected Long getBodyLength( final Object body )
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

    protected void addTraceInfo( final Trace trace, final WebResponse webResponse )
    {
        if ( trace != null )
        {
            trace.put( "status", webResponse.getStatus().value() );
            trace.put( "type", webResponse.getContentType().toString() );
            trace.put( "size", getSize( webResponse ) );
        }
    }

}