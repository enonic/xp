package com.enonic.xp.portal;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.HttpStatus;

@Beta
public final class PortalResponse
{
    private final int status;

    private final String contentType;

    private final Object body;

    private final ImmutableMap<String, String> headers;

    private final boolean postProcess;

    private final ImmutableListMultimap<HtmlTag, String> contributions;

    private final ImmutableList<Cookie> cookies;

    private final ImmutableList<String> filters;

    public PortalResponse( final Builder builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers.build();
        this.postProcess = builder.postProcess;
        this.contributions = builder.contributions.build();
        this.cookies = builder.cookies.build();
        this.filters = builder.filters.build();
    }

    public int getStatus()
    {
        return this.status;
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public ImmutableMap<String, String> getHeaders()
    {
        return this.headers;
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    public ImmutableList<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.containsKey( tag ) ? this.contributions.get( tag ) : ImmutableList.of();
    }

    public boolean hasContributions()
    {
        return !this.contributions.isEmpty();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getAsString()
    {
        return ( this.body != null ) ? this.body.toString() : null;
    }

    public ImmutableList<Cookie> getCookies()
    {
        return cookies;
    }

    public ImmutableList<String> getFilters()
    {
        return filters;
    }

    public static Builder create( final PortalResponse source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private Object body;

        private ImmutableMap.Builder<String, String> headers;

        private String contentType = "text/plain; charset=utf-8";

        private boolean postProcess = true;

        private ImmutableListMultimap.Builder<HtmlTag, String> contributions;

        private int status = HttpStatus.OK.value();

        private ImmutableList.Builder<Cookie> cookies;

        private ImmutableList.Builder<String> filters;

        private Builder()
        {
            clearHeaders();
            clearContributions();
            clearCookies();
            clearFilters();
        }

        private Builder( final PortalResponse source )
        {
            this.body = source.body;
            headers( source.headers );
            this.contentType = source.contentType;
            this.postProcess = source.postProcess;
            contributions( source.contributions );
            this.status = source.status;
            cookies( source.cookies );
            filters( source.filters );
        }

        public Builder body( final Object body )
        {
            this.body = body;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.putAll( headers );
            return this;
        }

        public Builder header( final String key, final String value )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.put( key, value );
            return this;
        }

        public Builder clearHeaders()
        {
            headers = ImmutableSortedMap.orderedBy( String.CASE_INSENSITIVE_ORDER );
            return this;
        }

        public Builder cookies( final List<Cookie> cookies )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.addAll( cookies );
            return this;
        }

        public Builder cookie( final Cookie cookie )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.add( cookie );
            return this;
        }

        public Builder clearCookies()
        {
            this.cookies = ImmutableList.builder();
            return this;
        }

        public Builder contentType( String contentType )
        {
            if ( contentType != null )
            {
                if ( contentType.indexOf( "charset" ) < 1 && contentType.startsWith( "text/html" ) )
                {
                    contentType += "; charset=utf-8";
                }
            }
            this.contentType = contentType;
            return this;
        }

        public Builder postProcess( final boolean postProcess )
        {
            this.postProcess = postProcess;
            return this;
        }

        public Builder contributions( final ListMultimap<HtmlTag, String> contributions )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( contributions );
            return this;
        }

        public Builder contribution( final HtmlTag tag, final String value )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.put( tag, value );
            return this;
        }

        public Builder contributionsFrom( final PortalResponse portalResponse )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( portalResponse.contributions );
            return this;
        }

        public Builder clearContributions()
        {
            this.contributions = ImmutableListMultimap.builder();
            return this;
        }

        public Builder status( final int status )
        {
            this.status = status;
            return this;
        }

        public Builder filters( final Iterable<String> filters )
        {
            if ( this.filters == null )
            {
                clearFilters();
            }
            this.filters.addAll( filters );
            return this;
        }

        public Builder filter( final String filter )
        {
            if ( this.filters == null )
            {
                clearFilters();
            }
            this.filters.add( filter );
            return this;
        }

        public Builder clearFilters()
        {
            filters = ImmutableList.builder();
            return this;
        }

        public PortalResponse build()
        {
            return new PortalResponse( this );
        }
    }
}
