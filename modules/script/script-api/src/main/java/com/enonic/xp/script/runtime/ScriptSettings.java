package com.enonic.xp.script.runtime;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public final class ScriptSettings
{
    private final ImmutableMap<Class, Supplier> bindings;

    private final ImmutableMap<String, Object> globalMap;

    private final DebugSettings debug;

    private ScriptSettings( final Builder builder )
    {
        this.globalMap = builder.globalMap.build();
        this.bindings = builder.attributes.build();
        this.debug = builder.debug;
    }

    public Map<String, Object> getGlobalVariables()
    {
        return this.globalMap;
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

    public DebugSettings getDebug()
    {
        return this.debug;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<Class, Supplier> attributes;

        private final ImmutableMap.Builder<String, Object> globalMap;

        private DebugSettings debug;

        private Builder()
        {
            this.attributes = ImmutableMap.builder();
            this.globalMap = ImmutableMap.builder();
        }

        public Builder globalVariable( final String name, final Object value )
        {
            this.globalMap.put( name, value );
            return this;
        }

        public <T> Builder binding( final Class<T> type, final Supplier<T> supplier )
        {
            this.attributes.put( type, supplier );
            return this;
        }

        public Builder debug( final DebugSettings debug )
        {
            this.debug = debug;
            return this;
        }

        public ScriptSettings build()
        {
            return new ScriptSettings( this );
        }
    }
}
