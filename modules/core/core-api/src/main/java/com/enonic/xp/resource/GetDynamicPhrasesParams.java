package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;


public final class GetDynamicPhrasesParams
{
    private final ApplicationKey key;

    private final String name;

    private GetDynamicPhrasesParams( final Builder builder )
    {
        this.key = builder.key;
        this.name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String name;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( name, "name is required" );
        }

        public GetDynamicPhrasesParams build()
        {
            validate();
            return new GetDynamicPhrasesParams( this );
        }
    }
}
