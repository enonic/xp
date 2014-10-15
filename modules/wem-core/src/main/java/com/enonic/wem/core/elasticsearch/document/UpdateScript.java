package com.enonic.wem.core.elasticsearch.document;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.Value;

public class UpdateScript
{
    private final String script;

    private final ImmutableMap<String, Value> params;

    private UpdateScript( final Builder builder )
    {
        script = builder.script;
        params = ImmutableMap.copyOf( builder.params );
    }

    public String getScript()
    {
        return script;
    }

    public ImmutableMap<String, Value> getParams()
    {
        return params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String script;

        private final Map<String, Value> params = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder script( String script )
        {
            this.script = script;
            return this;
        }

        public Builder addParam( final String name, final Value value )
        {
            this.params.put( name, value );
            return this;
        }

        public UpdateScript build()
        {
            return new UpdateScript( this );
        }
    }
}
