package com.enonic.wem.portal.internal.script.lib;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentPath;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

abstract class PortalUrlBuilder<T extends PortalUrlBuilder>
{
    private static final String PORTAL = "portal";

    private static final String DEFAULT_MODE = "live";

    private static final String DEFAULT_WORKSPACE = "stage";

    private static final String PUBLIC_RESOURCE = "public";

    private static final String SERVICE_RESOURCE = "service";

    private final String baseUrl;

    private String contentPath;

    private String resourcePath;

    private String mode;

    private String resourceType; // "public", "service", "image"

    private String module;

    private String workspace;

    private final Map<String, String> params;

    protected PortalUrlBuilder( final String baseUrl )
    {
        this.baseUrl = removeEnd( baseUrl, "/" );
        this.resourcePath = "";
        this.contentPath = "";
        this.resourceType = "";
        this.mode = DEFAULT_MODE;
        this.module = "";
        this.workspace = "";
        this.params = Maps.newLinkedHashMap();
    }

    public T mode( final String mode )
    {
        this.mode = mode == null ? DEFAULT_MODE : mode;
        return typecastToUrlBuilder( this );
    }

    public T workspace( final String workspace )
    {
        this.workspace = workspace == null ? DEFAULT_WORKSPACE : workspace;
        return typecastToUrlBuilder( this );
    }

    public T resourcePath( final String path )
    {
        this.resourcePath = nullToEmpty( path );
        return typecastToUrlBuilder( this );
    }

    public T resourceType( final String resourceType )
    {
        this.resourceType = nullToEmpty( resourceType );
        return typecastToUrlBuilder( this );
    }

    public T params( final Map<String, Object> params )
    {
        for ( final String name : params.keySet() )
        {
            param( name, params.get( name ) );
        }
        return typecastToUrlBuilder( this );
    }

    public T param( final String name, final Object value )
    {
        final String valueString = value == null ? null : urlEncode( value.toString() );
        this.params.put( urlEncode( name ), valueString );
        return typecastToUrlBuilder( this );
    }

    public T contentPath( final String contentPath )
    {
        this.contentPath = nullToEmpty( contentPath );
        return typecastToUrlBuilder( this );
    }

    public T contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath == null ? "" : contentPath.toString();
        return typecastToUrlBuilder( this );
    }

    public T module( final String module )
    {
        this.module = nullToEmpty( module );
        return typecastToUrlBuilder( this );
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

    protected void beforeBuildUrl()
    {
    }

    private String buildUrl()
    {
        final StringBuilder str = new StringBuilder( this.baseUrl ).
            append( "/" ).
            append( PORTAL ).
            append( "/" ).
            append( this.mode ).
            append( "/" ).
            append( this.workspace ).
            append( "/" );

        append( str, contentPath );

        if ( !resourceType.isEmpty() )
        {
            append( str, "/_/" + resourceType );
            if ( ( PUBLIC_RESOURCE.equals( resourceType ) || SERVICE_RESOURCE.equals( resourceType ) ) && !module.isEmpty() )
            {
                str.append( "/" ).append( module );
            }
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
        final boolean endsWithSlash = ( sb.length() > 0 ) && ( sb.charAt( sb.length() - 1 ) == '/' );
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
        beforeBuildUrl();
        return this.buildUrl();
    }

    @SuppressWarnings("unchecked")
    private T typecastToUrlBuilder( final Object object )
    {
        return (T) object;
    }
}
