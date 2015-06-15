package com.enonic.xp.portal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.annotations.Beta;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.portal.postprocess.HtmlTag;

@Beta
public final class PortalResponse
{
    public final static int STATUS_OK = 200;

    public final static int STATUS_METHOD_NOT_ALLOWED = 405;

    private int status;

    private String contentType;

    private Object body;

    private final Map<String, String> headers;

    private final Map<String, Object> options;

    private boolean postProcess;

    private final ListMultimap<HtmlTag, String> contributions;

    public PortalResponse( final Builder builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers;
        this.postProcess = builder.postProcess;
        this.contributions = builder.contributions;
        this.options = builder.options;
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

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    public List<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.containsKey( tag ) ? this.contributions.get( tag ) : Collections.emptyList();
    }

    public Map<String, Object> getOptions()
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
        return new Builder().
            body( source.body ).
            headers( source.headers ).
            contentType( source.contentType ).
            postProcess( source.postProcess ).
            contributions( source.contributions ).
            status( source.status ).
            options( source.options );
    }

    public static class Builder
    {
        private Object body;

        private Map<String, String> headers;

        private Map<String, Object> options;

        private String contentType = "text/plain; charset=utf-8";

        private boolean postProcess = true;

        private ListMultimap<HtmlTag, String> contributions;

        private int status = STATUS_OK;

        {
            clearHeaders();
            clearOptions();
            clearContributions();
        }


        public Builder body( final Object body )
        {
            this.body = body;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            this.headers = headers;
            return this;
        }

        public Builder header( final String key, final String value )
        {
            if ( headers == null )
            {
                clearHeaders();
            }
            this.headers.put( key, value );
            return this;
        }

        public Builder clearHeaders()
        {
            headers = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
            return this;
        }

        public Builder options( final Map<String, Object> options )
        {
            this.options = options;
            return this;
        }

        public Builder option( final String key, final Object value )
        {
            if ( this.options == null )
            {
                clearHeaders();
            }
            this.options.put( key, value );
            return this;
        }

        public Builder clearOptions()
        {
            options = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
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
            this.contributions = contributions;
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

        public Builder clearContributions()
        {
            this.contributions = ArrayListMultimap.create();
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
