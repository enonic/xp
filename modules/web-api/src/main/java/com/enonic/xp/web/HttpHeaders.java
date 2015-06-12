package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

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
        // TODO: Return the first value or null
        return null;
    }

    public Multimap<String, String> getAsMap()
    {
        return this.map;
    }

    public Set<HttpMethod> getAllow()
    {
        // TODO: Get allow header
        return null;
    }

    public void setAllow( final HttpMethod... values )
    {
        // TODO: Set allow header
    }

    public MediaType getContentType()
    {
        // TODO: Return contnet type header or null
        return null;
    }

    public void setContentType( final MediaType value )
    {
        // TODO: Set content type header
    }

    public long getContentLength()
    {
        // TODO: Return content-length header or -1
        return -1;
    }

    public void setContentLength( final long value )
    {
        // TODO: Set content-length header
    }

    public Instant getDate()
    {
        // TODO: Return the date header or null
        return null;
    }

    public void setDate( final Instant value )
    {
        // TODO: Set date header
    }

    public Instant getLastModified()
    {
        // TODO: Return the last modified header or null
        return null;
    }

    public void setLastModified( final Instant value )
    {
        // TODO: Set last modified header
    }

    public URI getLocation()
    {
        // TODO: Return the location header or null
        return null;
    }

    public void setLocation( final URI value )
    {
        // TODO: Set the location header
    }

    public Instant getExpires()
    {
        // TODO: Returns the expires header
        return null;
    }

    public void setExpires( final Instant value )
    {
        // TODO: Set the expires header
    }

    public String getReferer()
    {
        // TODO: Returns the referer header
        return null;
    }

    public void setReferer( final String value )
    {
        // TODO: Set referer header
    }
}
