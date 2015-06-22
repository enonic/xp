package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.MediaType;

public final class WebRequest
{
    private final HttpServletRequest raw;

    private HttpMethod httpMethod;

    private HttpHeaders httpHeaders;

    private WebRequest( final HttpServletRequest raw )
    {
        this.raw = raw;
    }

    public String getPath()
    {
        return this.raw.getPathInfo();
    }

    public HttpMethod getMethod()
    {
        if ( this.httpMethod == null )
        {
            this.httpMethod = HttpMethod.valueOf( raw.getMethod() );
        }

        return this.httpMethod;
    }

    public HttpHeaders getHeaders()
    {
        if ( this.httpHeaders == null )
        {
            initHttpHeaders();
        }

        return this.httpHeaders;
    }

    private void initHttpHeaders()
    {
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.setLastModified( getParsedDateHeaderValue( HttpHeaders.LAST_MODIFIED ) );
        this.httpHeaders.setExpires( getParsedDateHeaderValue( HttpHeaders.EXPIRES ) );
        this.httpHeaders.setDate( getParsedDateHeaderValue( HttpHeaders.DATE ) );
        this.httpHeaders.setReferer( getParsedUriHeaderValue( HttpHeaders.REFERER ) );
        this.httpHeaders.setLocation( getParsedUriHeaderValue( HttpHeaders.LOCATION ) );
        this.httpHeaders.setAllow( getParsedHttpMethodHeaderValue( HttpHeaders.ALLOW ) );
        this.httpHeaders.setContentLength( getParsedLongHeaderValue( HttpHeaders.CONTENT_LENGTH ) );
        this.httpHeaders.setContentType( getParsedMediaTypeHeaderValue( HttpHeaders.CONTENT_TYPE ) );
    }

    private HttpMethod[] getParsedHttpMethodHeaderValue( final String header )
    {
        final String value = this.raw.getHeader( header );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }
        final Iterable<String> tokens = Splitter.on( "," ).omitEmptyStrings().trimResults().split( value );
        return Lists.newArrayList( tokens ).stream().map( HttpMethod::valueOf ).toArray( size -> new HttpMethod[size] );
    }

    private Instant getParsedDateHeaderValue( final String header )
    {
        final String value = this.raw.getHeader( header );
        return StringUtils.isEmpty( value ) ? null : Instant.parse( value );
    }

    private URI getParsedUriHeaderValue( final String header )
    {
        final String value = this.raw.getHeader( header );
        return StringUtils.isEmpty( value ) ? null : URI.create( value );
    }

    private MediaType getParsedMediaTypeHeaderValue( final String header )
    {
        final String value = this.raw.getHeader( header );
        return StringUtils.isEmpty( value ) ? null : MediaType.parse( value );
    }

    private long getParsedLongHeaderValue( final String header )
    {
        final String value = this.raw.getHeader( header );
        return StringUtils.isEmpty( value ) ? -1 : Long.valueOf( value );
    }

    public HostAndPort getLocalAddress()
    {
        return HostAndPort.fromParts( this.raw.getLocalName(), this.raw.getLocalPort() );
    }

    public HostAndPort getRemoteAddress()
    {
        return HostAndPort.fromParts( this.raw.getRemoteHost(), this.raw.getRemotePort() );
    }

    public final HttpServletRequest getRawRequest()
    {
        return this.raw;
    }

    public static WebRequest from( final HttpServletRequest req )
    {
        return new WebRequest( req );
    }
}
