package com.enonic.wem.api.snapshot;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class RestoreResult
{
    final Set<String> indices;

    final String name;

    private RestoreResult( Builder builder )
    {
        indices = builder.indices;
        name = builder.name;
    }

    public Set<String> getIndices()
    {
        return indices;
    }

    public String getName()
    {
        return name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Set<String> indices = Sets.newHashSet();

        private String name;

        private Builder()
        {
        }

        public Builder indices( final Collection<String> indices )
        {
            this.indices.addAll( indices );
            return this;
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public RestoreResult build()
        {
            return new RestoreResult( this );
        }
    }
}
