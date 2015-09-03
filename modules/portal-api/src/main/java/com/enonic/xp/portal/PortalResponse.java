package com.enonic.xp.portal;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.portal.postprocess.HtmlTag;

@Beta
public final class PortalResponse
{
    public final static int STATUS_OK = 200;

    public final static int STATUS_METHOD_NOT_ALLOWED = 405;

    private final int status;

    private final String contentType;

    private final Object body;

    private final ImmutableMap<String, String> headers;

    private final ImmutableMap<String, Object> options;

    private final boolean postProcess;

    private final ImmutableListMultimap<HtmlTag, String> contributions;

    public PortalResponse( final Builder builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers.build();
        this.postProcess = builder.postProcess;
        this.contributions = builder.contributions.build();
        this.options = builder.options.build();
    }

    // TODO: Use HttpStatus
    public int getStatus()
    {
        return this.status;
    }

    // TODO: Use Guava MediaType
    public String getContentType()
    {
        return this.contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    // TODO: Expose map, not immutable map
    public ImmutableMap<String, String> getHeaders()
    {
        return this.headers;
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    // TODO: Expose map, not immutable map
    public ImmutableList<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.containsKey( tag ) ? this.contributions.get( tag ) : ImmutableList.of();
    }

    public boolean hasContributions()
    {
        return !this.contributions.isEmpty();
    }

    // TODO: Remove. Not needed.
    public ImmutableMap<String, Object> getOptions()
    {
        return options;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getAsString()
    {
        return ( this.body != null ) ? this.body.toString() : null;
    }

    public static Builder create( final PortalResponse source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private Object body;

        private ImmutableMap.Builder<String, String> headers;

        private ImmutableMap.Builder<String, Object> options;

        private String contentType = "text/plain; charset=utf-8";

        private boolean postProcess = true;

        private ImmutableListMultimap.Builder<HtmlTag, String> contributions;

        private int status = STATUS_OK;

        private Builder()
        {
            clearHeaders();
            clearOptions();
            clearContributions();
        }

        private Builder( final PortalResponse source )
        {
            this.body = source.body;
            headers( source.headers );
            options( source.options );
            this.contentType = source.contentType;
            this.postProcess = source.postProcess;
            contributions( source.contributions );
            this.status = source.status;
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

        public Builder options( final Map<String, Object> options )
        {
            if ( this.options == null )
            {
                clearOptions();
            }
            this.options.putAll( options );
            return this;
        }

        public Builder option( final String key, final Object value )
        {
            if ( this.options == null )
            {
                clearOptions();
            }
            this.options.put( key, value );
            return this;
        }

        public Builder clearOptions()
        {
            options = ImmutableSortedMap.orderedBy( String.CASE_INSENSITIVE_ORDER );
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

        public PortalResponse build()
        {
            return new PortalResponse( this );
        }
    }
}
