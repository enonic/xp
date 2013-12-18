package com.enonic.wem.portal.script.lib;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public final class PortalUrlBuilder
{
    private static final String PORTAL = "portal";

    private static final String DEFAULT_MODE = "live";

    private final String baseUrl;

    private String contentPath;

    private String resourcePath;

    private String mode;

    private String service; // "public", "service", "image"

    private final Map<String, String> params;

    private PortalUrlBuilder( final String baseUrl )
    {
        this.baseUrl = removeEnd( baseUrl, "/" );
        this.resourcePath = "";
        this.contentPath = "";
        this.service = "";
        this.mode = DEFAULT_MODE;
        this.params = Maps.newLinkedHashMap();
    }

    public PortalUrlBuilder mode( final String mode )
    {
        this.mode = mode == null ? DEFAULT_MODE : mode;
        return this;
    }

    public PortalUrlBuilder resourcePath( final String path )
    {
        this.resourcePath = nullToEmpty( path );
        return this;
    }

    public PortalUrlBuilder service( final String service )
    {
        this.service = nullToEmpty( service );
        return this;
    }

    public PortalUrlBuilder params( final Map<String, Object> params )
    {
        for ( final String name : params.keySet() )
        {
            param( name, params.get( name ) );
        }
        return this;
    }

    public PortalUrlBuilder param( final String name, final Object value )
    {
        final String valueString = value == null ? null : urlEncode( value.toString() );
        this.params.put( urlEncode( name ), valueString );
        return this;
    }

    public PortalUrlBuilder contentPath( final String contentPath )
    {
        this.contentPath = nullToEmpty( contentPath );
        return this;
    }

    private String urlEncode( final String value )
    {
        try
        {
            return URLEncoder.encode( value, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private String serializeParams( final Map<String, String> params )
    {
        if ( params.isEmpty() )
        {
            return "";
        }
        Joiner.MapJoiner joiner = Joiner.on( "&" ).useForNull( "" ).withKeyValueSeparator( "=" );
        return "?" + joiner.join( params );
    }

    private String buildUrl()
    {
        final StringBuilder str = new StringBuilder( this.baseUrl ).append( "/" ).append( PORTAL ).
            append( "/" ).append( this.mode ).append( "/" );
        append( str, contentPath );
        if ( !service.isEmpty() )
        {
            append( str, "/_/" + service );
        }
        append( str, resourcePath );
        str.append( serializeParams( this.params ) );
        return str.toString();
    }

    private void append( final StringBuilder sb, final String urlPart )
    {
        if ( isNullOrEmpty( urlPart ) )
        {
            return;
        }
        final boolean endsWithSlash = ( sb.length() > 0 ) && sb.charAt( sb.length() - 1 ) == '/';
        final boolean startsWithSlash = urlPart.charAt( 0 ) == '/';
        if ( endsWithSlash && startsWithSlash )
        {
            sb.append( removeStart( urlPart, "/" ) );
        }
        else if ( !endsWithSlash && !startsWithSlash )
        {
            sb.append( "/" ).append( urlPart );
        }
        else
        {
            sb.append( urlPart );
        }
    }

    @Override
    public String toString()
    {
        return this.buildUrl();
    }

    public static PortalUrlBuilder createUrl( final String baseUrl )
    {
        return new PortalUrlBuilder( baseUrl );
    }
}
