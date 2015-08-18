package com.enonic.xp.script.runtime;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public final class ScriptSettings
{
    private final String basePath;

    private final ImmutableMap<String, Object> globalMap;

    private final ImmutableMap<Class, Supplier> attributes;

    private ScriptSettings( final Builder builder )
    {
        this.basePath = builder.basePath;
        this.globalMap = builder.globalMap.build();
        this.attributes = builder.attributes.build();
    }

    public String getBasePath()
    {
        return this.basePath;
    }

    public Map<String, Object> getGlobalVariables()
    {
        return this.globalMap;
    }

    public <T> Supplier<T> getAttribute( final Class<T> type )
    {
        return typecast( this.attributes.get( type ) );
    }

    @SuppressWarnings("unchecked")
    private <T> Supplier<T> typecast( final Supplier value )
    {
        return (Supplier<T>) value;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private String basePath;

        private final ImmutableMap.Builder<String, Object> globalMap;

        private final ImmutableMap.Builder<Class, Supplier> attributes;

        private Builder()
        {
            this.globalMap = ImmutableMap.builder();
            this.attributes = ImmutableMap.builder();
        }

        public Builder basePath( final String basePath )
        {
            this.basePath = basePath;
            return this;
        }

        public Builder globalVariable( final String name, final Object value )
        {
            this.globalMap.put( name, value );
            return this;
        }

        public <T> Builder attribute( final Class<T> type, final Supplier<T> supplier )
        {
            this.attributes.put( type, supplier );
            return this;
        }

        public ScriptSettings build()
        {
            return new ScriptSettings( this );
        }
    }
}
