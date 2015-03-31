package com.enonic.xp.web.vhost.impl.mapping;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.enonic.xp.web.vhost.VirtualHost;

public final class VirtualHostMapping
    implements VirtualHost, Comparable<VirtualHostMapping>
{
    private final static String DEFAULT_HOST = "localhost";

    private final String name;

    private String host;

    private String source;

    private String target;

    public VirtualHostMapping( final String name )
    {
        this.name = name;
        setHost( null );
        setSource( null );
        setTarget( null );
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getHost()
    {
        return this.host;
    }

    @Override
    public String getSource()
    {
        return this.source;
    }

    @Override
    public String getTarget()
    {
        return this.target;
    }

    public void setHost( final String value )
    {
        this.host = Strings.isNullOrEmpty( value ) ? DEFAULT_HOST : value;
    }

    public void setSource( final String value )
    {
        this.source = normalizePath( value );
    }

    public void setTarget( final String value )
    {
        this.target = normalizePath( value );
    }

    public boolean matches( final HttpServletRequest req )
    {
        return matchesHost( req ) && matchesSource( req );
    }

    private boolean matchesHost( final HttpServletRequest req )
    {
        final String serverName = req.getServerName();
        return ( serverName != null ) && this.host.equalsIgnoreCase( serverName );
    }

    private boolean matchesSource( final HttpServletRequest req )
    {
        final String actualPath = req.getRequestURI();
        return "/".equals( this.source ) || actualPath.equals( this.source ) || actualPath.startsWith( this.source + "/" );
    }

    public String getFullTargetPath( final HttpServletRequest req )
    {
        String path = req.getRequestURI();
        if ( !"/".equals( this.source ) && path.startsWith( this.source ) )
        {
            path = path.substring( this.source.length() );
        }

        return normalizePath( this.target + path );
    }

    @Override
    public int compareTo( final VirtualHostMapping o )
    {
        final int compared = compareHost( o.host );
        if ( compared == 0 )
        {
            return compareSource( o.source );
        }
        else
        {
            return compared;
        }
    }

    private int compareHost( final String host )
    {
        return this.host.compareTo( host );
    }

    private int compareSource( final String source )
    {
        final int compared = source.length() - this.source.length();
        if ( compared == 0 )
        {
            return this.source.compareTo( source );
        }
        else
        {
            return compared;
        }
    }

    private String normalizePath( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return "/";
        }

        final Iterable<String> parts = Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value );
        return "/" + Joiner.on( '/' ).join( parts );
    }
}
