package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeStart;

public abstract class PortalUrlBuilder<T extends PortalUrlBuilder>
{
    private String baseUri = "/portal";

    private String renderMode;

    private String workspace;

    private String contentPath;

    private final Multimap<String, String> params;

    public PortalUrlBuilder()
    {
        this.params = HashMultimap.create();
    }

    public final T renderMode( final String value )
    {
        this.renderMode = Strings.isNullOrEmpty( value ) ? RenderMode.LIVE.toString() : value;
        return typecastThis();
    }

    public final T renderMode( final RenderMode value )
    {
        return renderMode( value != null ? value.toString() : null );
    }

    public final T workspace( final String value )
    {
        this.workspace = emptyToNull( value );
        return typecastThis();
    }

    public final T baseUri( final String value )
    {
        this.baseUri = nullToEmpty( value );
        return typecastThis();
    }

    public final T workspace( final Workspace value )
    {
        return workspace( value != null ? value.toString() : null );
    }

    public final T contentPath( final String value )
    {
        this.contentPath = emptyToNull( value );
        return typecastThis();
    }

    public final T contentPath( final ContentPath value )
    {
        return contentPath( value != null ? value.toString() : null );
    }

    public final T param( final String name, final Object value )
    {
        final String strValue = value != null ? value.toString() : null;
        this.params.put( name, strValue );
        return typecastThis();
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
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

    private void appendParams( final StringBuilder str, final Collection<Map.Entry<String, String>> params )
    {
        if ( params.isEmpty() )
        {
            return;
        }

        str.append( "?" );

        final Iterator<Map.Entry<String, String>> it = params.iterator();
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

    private String urlEncode( final String value )
    {
        return UrlEscapers.urlFormParameterEscaper().escape( value );
    }

    public final T params( final Multimap<String, String> params )
    {
        this.params.putAll( params );
        return typecastThis();
    }

    public final String build()
    {
        final StringBuilder str = new StringBuilder();
        appendPart( str, this.baseUri );

        final Multimap<String, String> params = HashMultimap.create();
        buildUrl( str, params );
        appendParams( str, params.entries() );

        final String uri = str.toString();
        return ServletRequestUrlHelper.rewriteUri( uri );
    }

    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        appendPart( url, this.renderMode );
        appendPart( url, this.workspace );
        appendPart( url, this.contentPath );

        params.putAll( this.params );
    }

    @Override
    public final String toString()
    {
        return build();
    }
}
