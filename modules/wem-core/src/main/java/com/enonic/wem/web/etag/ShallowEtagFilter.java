package com.enonic.wem.web.etag;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public final class ShallowEtagFilter
    implements Filter
{
    private final static String HEADER_ETAG = "ETag";

    private final static String HEADER_IF_NONE_MATCH = "If-None-Match";

    private final static Logger LOG = LoggerFactory.getLogger( ShallowEtagFilter.class );

    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        // Do nothing
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
    }

    private void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        final ShallowEtagResponseWrapper wrapper = new ShallowEtagResponseWrapper( res );
        chain.doFilter( req, wrapper );

        final byte[] body = wrapper.toByteArray();
        final String responseETag = generateETagHeaderValue( body );
        res.setHeader( HEADER_ETAG, responseETag );

        final String requestETag = req.getHeader( HEADER_IF_NONE_MATCH );
        if ( responseETag.equals( requestETag ) )
        {
            LOG.debug( "ETag [" + responseETag + "] equal to If-None-Match, sending 304" );
            res.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
        }
        else
        {
            LOG.debug( "ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag +
                           "], sending normal response" );
            res.setContentLength( body.length );
            ByteSource.wrap( body ).copyTo( res.getOutputStream() );
        }
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }

    private String generateETagHeaderValue( final byte[] bytes )
    {
        return "\"" + Hashing.md5().hashBytes( bytes ).toString() + "\"";
    }

}
