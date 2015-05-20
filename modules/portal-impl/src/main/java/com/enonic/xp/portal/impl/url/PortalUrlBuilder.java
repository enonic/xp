package com.enonic.xp.portal.impl.url;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.removeStart;

abstract class PortalUrlBuilder<T extends AbstractUrlParams>
{
    protected PortalContext context;

    protected T params;

    protected ContentService contentService;

    private String getBaseUri()
    {
        return this.context.getBaseUri();
    }

    private Branch getBranch()
    {
        return this.context.getBranch();
    }

    public final void setParams( final T params )
    {
        this.params = params;
        this.context = this.params.getContext();
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

    public final String build()
    {
        try
        {
            return doBuild();
        }
        catch ( final Exception e )
        {
            return buildErrorUrl( e );
        }
    }

    private String doBuild()
    {
        final StringBuilder str = new StringBuilder();
        appendPart( str, getBaseUri() );

        final Multimap<String, String> params = HashMultimap.create();
        buildUrl( str, params );
        appendParams( str, params.entries() );

        final String uri = str.toString();
        final String rewrittenUri = ServletRequestUrlHelper.rewriteUri( uri );
        return rewriteUri( rewrittenUri );
    }

    @SuppressWarnings("unchecked")
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        params.putAll( this.params.getParams() );
        appendPart( url, getBranch().toString() );
    }

    protected String rewriteUri( final String uri )
    {
        return uri;
    }

    protected String buildErrorUrl( final Exception e )
    {
        if ( e instanceof NotFoundException )
        {
            return buildErrorUrl( 404, e.getMessage() );
        }
        else
        {
            return buildErrorUrl( 500, e.getMessage() );
        }
    }

    protected final String buildErrorUrl( final int code, final String message )
    {
        final StringBuilder str = new StringBuilder();
        appendPart( str, getBaseUri() );
        appendPart( str, getBranch().toString() );
        appendPart( str, this.context.getContentPath().toString() );
        appendPart( str, "_" );
        appendPart( str, "error" );
        appendPart( str, String.valueOf( code ) );

        final Multimap<String, String> params = HashMultimap.create();

        if ( message != null )
        {
            params.put( "message", message );
        }

        appendParams( str, params.entries() );
        final String uri = str.toString();
        return ServletRequestUrlHelper.rewriteUri( uri );
    }

    @Override
    public final String toString()
    {
        return build();
    }
}
