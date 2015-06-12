package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.HashMultimap;
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
        Collection<String> headerValues = map.get( header );
        if ( headerValues != null )
        {
            Iterator<String> iter = headerValues.iterator();
            return iter.hasNext() ? iter.next() : null;
        }
        else
        {
            return null;
        }
    }

    public Multimap<String, String> getAsMap()
    {
        return this.map;
    }

    public void set( final String headerName, final String headerValue )
    {
        map.put( headerName, headerValue );
    }

    public Set<HttpMethod> getAllow()
    {
        String value = getFirst( ALLOW );
        if ( !StringUtils.isEmpty( value ) )
        {
            List<HttpMethod> allowedMethod = new ArrayList<HttpMethod>( 5 );
            String[] tokens = value.split( ",\\s*" );
            for ( String token : tokens )
            {
                allowedMethod.add( HttpMethod.valueOf( token ) );
            }
            return EnumSet.copyOf( allowedMethod );
        }
        else
        {
            return EnumSet.noneOf( HttpMethod.class );
        }
    }

    public void setAllow( final HttpMethod... values )
    {
        set( ALLOW, collectionToCommaDelimitedString( Arrays.asList( values ) ) );
    }

    public static String collectionToCommaDelimitedString( final Collection<?> coll )
    {
        if ( coll == null || coll.isEmpty() )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while ( it.hasNext() )
        {
            sb.append( it.next() );
            if ( it.hasNext() )
            {
                sb.append( "," );
            }
        }
        return sb.toString();
    }

    public MediaType getContentType()
    {
        String value = getFirst( CONTENT_TYPE );
        return ( StringUtils.isNotEmpty( value ) ? MediaType.parse( value ) : null );
    }

    public void setContentType( final MediaType mediaType )
    {
        set( CONTENT_TYPE, mediaType.toString() );
    }

    public long getContentLength()
    {
        String value = getFirst( CONTENT_LENGTH );
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
        String headerValue = getFirst( headerName );
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
        String value = getFirst( LOCATION );
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
        String value = getFirst( REFERER );
        return ( value != null ? URI.create( value ) : null );
    }

    public void setReferer( final URI value )
    {
        set( REFERER, value.toASCIIString() );
    }
}
