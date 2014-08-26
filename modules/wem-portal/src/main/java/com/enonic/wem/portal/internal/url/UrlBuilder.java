package com.enonic.wem.portal.internal.url;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentPath;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

final class UrlBuilder
{
    static final String PORTAL = "portal";

    static final String DEFAULT_MODE = "live";

    static final String DEFAULT_WORKSPACE = "stage";

    static final String PUBLIC_RESOURCE = "public";

    static final String SERVICE_RESOURCE = "service";

    private final String baseUrl;

    private String contentPath;

    private String resourcePath;

    private String mode;

    private String resourceType; // "public", "service", "image"

    private String module;

    private String workspace;

    private final Map<String, String> params;

    private Consumer<UrlBuilder> beforeBuildCallback;

    UrlBuilder( final String baseUrl )
    {
        this.baseUrl = removeEnd( baseUrl, "/" );
        this.resourcePath = "";
        this.contentPath = "";
        this.resourceType = "";
        this.mode = DEFAULT_MODE;
        this.module = "";
        this.workspace = "";
        this.params = Maps.newLinkedHashMap();
        this.beforeBuildCallback = null;
    }

    public UrlBuilder mode( final String mode )
    {
        this.mode = mode == null ? DEFAULT_MODE : mode;
        return this;
    }

    public UrlBuilder workspace( final String workspace )
    {
        this.workspace = workspace == null ? DEFAULT_WORKSPACE : workspace;
        return this;
    }

    public UrlBuilder resourcePath( final String path )
    {
        this.resourcePath = nullToEmpty( path );
        return this;
    }

    public UrlBuilder resourceType( final String resourceType )
    {
        this.resourceType = nullToEmpty( resourceType );
        return this;
    }

    public UrlBuilder params( final Map<String, Object> params )
    {
        for ( final String name : params.keySet() )
        {
            param( name, params.get( name ) );
        }
        return this;
    }

    public UrlBuilder param( final String name, final Object value )
    {
        final String valueString = value == null ? null : urlEncode( value.toString() );
        this.params.put( urlEncode( name ), valueString );
        return this;
    }

    public UrlBuilder contentPath( final String contentPath )
    {
        this.contentPath = nullToEmpty( contentPath );
        return this;
    }

    public UrlBuilder contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath == null ? "" : contentPath.toString();
        return this;
    }

    public UrlBuilder module( final String module )
    {
        this.module = nullToEmpty( module );
        return this;
    }

    public void beforeBuildUrl( Consumer<UrlBuilder> doBeforeBuildUrl )
    {
        this.beforeBuildCallback = doBeforeBuildUrl;
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

    private void doBeforeBuildUrl()
    {
        if ( beforeBuildCallback != null )
        {
            beforeBuildCallback.accept( this );
        }
    }

    private String buildUrl()
    {
        final StringBuilder str = new StringBuilder( this.baseUrl ).
            append( "/" ).
            append( PORTAL ).
            append( "/" ).
            append( this.mode ).
            append( "/" );
        if ( !workspace.isEmpty() )
        {
            str.append( this.workspace ).
                append( "/" );
        }

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
        doBeforeBuildUrl();
        return this.buildUrl();
    }

}
