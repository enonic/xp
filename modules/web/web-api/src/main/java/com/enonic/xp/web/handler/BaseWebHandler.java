package com.enonic.xp.web.handler;

import java.util.EnumSet;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

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
        this( order, EnumSet.allOf( HttpMethod.class ) );
    }

    public BaseWebHandler( final int order, final EnumSet<HttpMethod> methodsAllowed )
    {
        this.order = order;
        this.methodsAllowed = methodsAllowed;
    }

    protected abstract boolean canHandle( WebRequest webRequest );

    protected abstract void doHandle( WebRequest webRequest, WebResponse webResponse, WebHandlerChain webHandlerChain );

    private void checkMethodAllowed( final HttpMethod method )
    {
        if ( !methodsAllowed.contains( method ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
        }
    }

    private void handleOptions( final WebResponse webResponse )
    {
        webResponse.setStatus( HttpStatus.OK );
        webResponse.addHeader( "Allow", Joiner.on( "," ).join( this.methodsAllowed ) );
    }


    @Override
    public void handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        if ( canHandle( webRequest ) )
        {
            final HttpMethod method = webRequest.getMethod();
            checkMethodAllowed( method );

            if ( HttpMethod.OPTIONS == method )
            {
                handleOptions( webResponse );
                return;
            }

            doHandle( webRequest, webResponse, webHandlerChain );
        }
        else
        {
            webHandlerChain.handle( webRequest, webResponse );
        }

    }
}
