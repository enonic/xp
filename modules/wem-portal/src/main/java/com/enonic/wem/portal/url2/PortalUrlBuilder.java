package com.enonic.wem.portal.url2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentPath;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public abstract class PortalUrlBuilder<T extends PortalUrlBuilder>
{
    private static final String DEFAULT_MODE = "live";

    private static final String DEFAULT_WORKSPACE = "stage";

    private String baseUri;

    private String mode;

    private String workspace;

    private String contentPath;

    private final Map<String, String> params;

    public PortalUrlBuilder()
    {
        this.params = Maps.newLinkedHashMap();
        this.mode = DEFAULT_MODE;
        this.workspace = DEFAULT_WORKSPACE;
    }

    public final T baseUri( final String baseUri )
    {
        this.baseUri = emptyToNull( baseUri );
        return typecastThis();
    }

    public final T mode( final String mode )
    {
        this.mode = mode == null ? DEFAULT_MODE : mode;
        return typecastThis();
    }

    public final T workspace( final String workspace )
    {
        this.workspace = workspace == null ? DEFAULT_WORKSPACE : workspace;
        return typecastThis();
    }

    public final T param( final String name, final Object value )
    {
        this.params.put( name, value != null ? value.toString() : null );
        return typecastThis();
    }

    public final T contentPath( final String contentPath )
    {
        this.contentPath = emptyToNull( contentPath );
        return typecastThis();
    }

    public final T contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath == null ? "" : contentPath.toString();
        return typecastThis();
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
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

    protected void buildUrl( final StringBuilder url, final Map<String, String> params )
    {
        appendPart( url, this.mode );
        appendPart( url, this.workspace );
        appendPart( url, this.contentPath );

        params.putAll( this.params );
    }

    private String buildUrl()
    {
        final StringBuilder str = new StringBuilder();

        if ( this.baseUri != null )
        {
            str.append( removeEnd( this.baseUri, "/" ) );
        }

        appendPart( str, "/portal" );

        final Map<String, String> params = Maps.newLinkedHashMap();
        buildUrl( str, params );
        appendParams( str, params );

        return str.toString();
    }

    protected final void appendPart( final StringBuilder str, final String urlPart )
    {
        if ( isNullOrEmpty( urlPart ) )
        {
            return;
        }
        final boolean endsWithSlash = ( str.length() > 0 ) && ( str.charAt( str.length() - 1 ) == '/' );
        final boolean startsWithSlash = urlPart.charAt( 0 ) == '/';
        if ( endsWithSlash && startsWithSlash )
        {
            str.append( removeStart( urlPart, "/" ) );
        }
        else if ( !endsWithSlash && !startsWithSlash )
        {
            str.append( "/" ).append( urlPart );
        }
        else
        {
            str.append( urlPart );
        }
    }

    private void appendParams( final StringBuilder str, final Map<String, String> params )
    {
        if ( params.isEmpty() )
        {
            return;
        }

        str.append( "?" );

        final Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        appendParam( str, it.next() );

        while ( it.hasNext() )
        {
            str.append( "&" );
            appendParam( str, it.next() );
        }
    }

    private void appendParam( final StringBuilder str, final Map.Entry<String, String> param )
    {
        appendParam( str, param.getKey(), param.getValue() );
    }

    private void appendParam( final StringBuilder str, final String key, final String value )
    {
        final String encoded = urlEncode( nullToEmpty( value ) );
        str.append( key ).append( "=" ).append( encoded );
    }

    public final String toString()
    {
        return buildUrl();
    }
}
