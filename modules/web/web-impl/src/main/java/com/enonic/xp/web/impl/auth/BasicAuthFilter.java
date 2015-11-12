package com.enonic.xp.web.impl.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=40", "osgi.http.whiteboard.filter.dispatcher=FORWARD",
        "osgi.http.whiteboard.filter.dispatcher=REQUEST"})
public final class BasicAuthFilter
    extends OncePerRequestFilter
{
    private SecurityService securityService;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        login( req );
        chain.doFilter( req, res );
    }

    private void login( final HttpServletRequest req )
    {
        final String header = req.getHeader( HttpHeaders.AUTHORIZATION );
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

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    private static String[] parseHeader( final String header )
    {
        if ( header.length() < 6 )
        {
            return null;
        }

        final String type = header.substring( 0, 5 ).toUpperCase();
        if ( !type.equals( HttpServletRequest.BASIC_AUTH ) )
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
