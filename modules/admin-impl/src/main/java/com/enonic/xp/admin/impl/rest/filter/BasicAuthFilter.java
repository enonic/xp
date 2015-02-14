package com.enonic.xp.admin.impl.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

import com.enonic.xp.admin.impl.security.AuthHelper;
import com.enonic.wem.api.security.SecurityService;

@Priority(Priorities.AUTHENTICATION)
final class BasicAuthFilter
    implements ContainerRequestFilter
{
    private final SecurityService securityService;

    public BasicAuthFilter( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    public void filter( final ContainerRequestContext req )
        throws IOException
    {
        final String header = req.getHeaderString( "authorization" );
        if ( header == null )
        {
            return;
        }

        final String[] parts = parseHeader( header );
        if ( parts == null )
        {
            return;
        }

        final AuthHelper helper = new AuthHelper( this.securityService );
        helper.login( parts[0], parts[1], false );
    }

    public static String[] parseHeader( final String header )
    {
        if ( header.length() < 6 )
        {
            return null;
        }

        final String type = header.substring( 0, 5 ).toLowerCase();
        if ( !type.equals( "basic" ) )
        {
            return null;
        }

        final String val = header.substring( 6 );
        final BaseEncoding encoding = BaseEncoding.base64();

        final String decoded = new String( encoding.decode( val ), Charsets.UTF_8 );
        final String[] parts = decoded.split( ":" );

        if ( parts.length != 2 )
        {
            return null;
        }

        return parts;
    }
}
