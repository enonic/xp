package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.portal.impl.PortalHandler2;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

public abstract class BaseHandler
    implements PortalHandler2
{
    private final int order;

    private EnumSet<HttpMethod> methodsAllowed;

    public BaseHandler( final int order )
    {
        this.order = order;
        setMethodsAllowed( EnumSet.allOf( HttpMethod.class ) );
    }

    protected final void setMethodsAllowed( final HttpMethod... methods )
    {
        setMethodsAllowed( EnumSet.copyOf( Lists.newArrayList( methods ) ) );
    }

    protected final void setMethodsAllowed( final EnumSet<HttpMethod> methods )
    {
        this.methodsAllowed = Sets.newEnumSet( methods, HttpMethod.class );
        this.methodsAllowed.add( HttpMethod.OPTIONS );
    }

    @Override
    public final int getOrder()
    {
        return this.order;
    }

    @Override
    public final PortalResponse handle( final PortalRequest req )
        throws Exception
    {
        final HttpMethod method = HttpMethod.valueOf( req.getMethod() );
        checkMethodAllowed( method );

        if ( method == HttpMethod.OPTIONS )
        {
            return handleOptions();
        }

        return doHandle( req );
    }

    protected abstract PortalResponse doHandle( final PortalRequest req )
        throws Exception;

    protected final PortalException notFound( final String message, final Object... args )
    {
        return PortalException.notFound( String.format( message, args ) );
    }

    protected final PortalException methodNotAllowed( final String message, final Object... args )
    {
        return new PortalException( HttpStatus.METHOD_NOT_ALLOWED, String.format( message, args ) );
    }

    private void checkMethodAllowed( final HttpMethod method )
    {
        if ( this.methodsAllowed.contains( method ) )
        {
            return;
        }

        throw methodNotAllowed( "Method %s not allowed", method );
    }

    private PortalResponse handleOptions()
    {
        return PortalResponse.create().
            status( 200 ).
            header( "Allow", Joiner.on( "," ).join( this.methodsAllowed ) ).
            build();
    }
}
