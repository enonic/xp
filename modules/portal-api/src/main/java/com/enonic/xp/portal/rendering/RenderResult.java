package com.enonic.xp.portal.rendering;

import java.util.Map;
import java.util.TreeMap;

import com.google.common.annotations.Beta;

@Beta
public final class RenderResult
{
    protected int status = 200;

    protected String type = "text/plain";

    protected Object entity;

    protected final Map<String, String> headers;

    private RenderResult()
    {
        this.headers = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
    }

    public int getStatus()
    {
        return this.status;
    }

    public String getType()
    {
        return this.type;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public Object getEntity()
    {
        return this.entity;
    }

    public String getAsString()
    {
        return ( this.entity != null ) ? this.entity.toString() : null;
    }

    public static Builder newRenderResult()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private final RenderResult result;

        private Builder()
        {
            this.result = new RenderResult();
        }

        public Builder status( final int status )
        {
            this.result.status = status;
            return this;
        }

        public Builder type( final String type )
        {
            this.result.type = type;
            return this;
        }

        public Builder entity( final Object entity )
        {
            this.result.entity = entity;
            return this;
        }

        public Builder header( final String name, final String value )
        {
            this.result.headers.put( name, value );
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            this.result.headers.putAll( headers );
            return this;
        }

        public RenderResult build()
        {
            return this.result;
        }
    }
}
