package com.enonic.xp.admin.impl.app;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=xp"})
@Order(-20)
@WebFilter("/admin/*")
public class NoCacheAdminFilter
    extends OncePerRequestFilter
{
    private static final String PRIVATE_NO_CACHE = "private, no-cache";

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        res.setHeader( HttpHeaders.CACHE_CONTROL, PRIVATE_NO_CACHE );
        chain.doFilter( req, new NoCacheAdminResponseWrapper( res ) );
    }

    static class NoCacheAdminResponseWrapper
        extends HttpServletResponseWrapper
    {
        public NoCacheAdminResponseWrapper( final HttpServletResponse res )
        {
            super( res );
        }

        @Override
        public void setHeader( final String name, String value )
        {
            if ( HttpHeaders.CACHE_CONTROL.equalsIgnoreCase( name ) )
            {
                if ( value != null )
                {
                    if ( value.contains( "private" ) )
                    {
                        return;
                    }
                    else if ( value.contains( "public" ) )
                    {
                        value = value.replaceAll( "public", "private" );
                    }
                    else
                    {
                        value = "private, " + value;
                    }
                }
                else
                {
                    value = PRIVATE_NO_CACHE;
                }
            }
            super.setHeader( name, value );
        }

        @Override
        public void addHeader( final String name, String value )
        {
            if ( value != null && HttpHeaders.CACHE_CONTROL.equalsIgnoreCase( name ) )
            {
                value = value.replaceAll( "public", "private" );
            }
            super.addHeader( name, value );
        }
    }
}
