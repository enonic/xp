package com.enonic.xp.script.runtime;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public final class ScriptSettings
{
    private final String basePath;

    private final ImmutableMap<Class, Supplier> bindings;

    private ScriptSettings( final Builder builder )
    {
        this.basePath = builder.basePath;
        this.bindings = builder.attributes.build();
    }

    public String getBasePath()
    {
        return this.basePath != null ? this.basePath : "";
    }

    public <T> Supplier<T> getBinding( final Class<T> type )
    {
        return typecast( this.bindings.get( type ) );
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

        private final ImmutableMap.Builder<Class, Supplier> attributes;

        private Builder()
        {
            this.attributes = ImmutableMap.builder();
        }

        public Builder basePath( final String basePath )
        {
            this.basePath = basePath;
            return this;
        }

        public <T> Builder binding( final Class<T> type, final Supplier<T> supplier )
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
