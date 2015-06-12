package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

public final class HttpHeaders
{
    public static final String ALLOW = "Allow";

    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String DATE = "Date";

    public static final String EXPIRES = "Expires";

    public static final String LAST_MODIFIED = "Last-Modified";

    public static final String LOCATION = "Location";

    public static final String REFERER = "Referer";

    private final Multimap<String, String> map;

    public HttpHeaders()
    {
        this( HashMultimap.create() );
    }

    public HttpHeaders( final Multimap<String, String> map )
    {
        this.map = map;
    }

    public String getFirst( final String header )
    {
        final Collection<String> headerValues = this.map.get( header );
        final Iterator<String> iter = headerValues.iterator();
        return iter.hasNext() ? iter.next() : null;
    }

    public Multimap<String, String> getAsMap()
    {
        return this.map;
    }

    public void set( final String headerName, final String headerValue )
    {
        this.map.put( headerName, headerValue );
    }

    public Set<HttpMethod> getAllow()
    {
        final String value = getFirst( ALLOW );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return EnumSet.noneOf( HttpMethod.class );
        }

        final Iterable<String> parts = Splitter.on( "," ).omitEmptyStrings().trimResults().split( value );
        return Lists.newArrayList( parts ).stream().map( HttpMethod::valueOf ).collect( Collectors.toSet() );
    }

    public void setAllow( final HttpMethod... values )
    {
        final String value = Joiner.on( "," ).skipNulls().join( values );
        set( ALLOW, value );
    }

    public MediaType getContentType()
    {
        final String value = getFirst( CONTENT_TYPE );
        return ( Strings.isNullOrEmpty( value ) ? null : MediaType.parse( value ) );
    }

    public void setContentType( final MediaType mediaType )
    {
        set( CONTENT_TYPE, mediaType.toString() );
    }

    public long getContentLength()
    {
        final String value = getFirst( CONTENT_LENGTH );
        return ( value != null ? Long.parseLong( value ) : -1 );
    }

    public void setContentLength( final long contentLength )
    {
        set( CONTENT_LENGTH, Long.toString( contentLength ) );
    }

    public Instant getDate()
    {
        return getFirstDate( DATE );
    }

    public Instant getFirstDate( final String headerName )
    {
        final String headerValue = getFirst( headerName );
        if ( headerValue == null )
        {
            return null;
        }

        return Instant.parse( headerValue );
    }

    public void setDate( final Instant value )
    {
        setDate( DATE, value );
    }

    public void setDate( final String headerName, final Instant date )
    {
        set( headerName, date.toString() );
    }

    public Instant getLastModified()
    {
        return getFirstDate( LAST_MODIFIED );
    }

    public void setLastModified( final Instant lastModified )
    {
        setDate( LAST_MODIFIED, lastModified );
    }

    public URI getLocation()
    {
        final String value = getFirst( LOCATION );
        return ( value != null ? URI.create( value ) : null );
    }

    public void setLocation( final URI location )
    {
        set( LOCATION, location.toASCIIString() );
    }

    public Instant getExpires()
    {
        return getFirstDate( EXPIRES );
    }

    public void setExpires( final Instant value )
    {
        setDate( EXPIRES, value );
    }

    public URI getReferer()
    {
        final String value = getFirst( REFERER );
        return ( value != null ? URI.create( value ) : null );
    }

    public void setReferer( final URI value )
    {
        set( REFERER, value.toASCIIString() );
    }
}
