package com.enonic.xp.schema;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;


public final class UpdateStylesParams
{
    private final ApplicationKey key;

    private final String resource;

    private UpdateStylesParams( final Builder builder )
    {
        this.key = builder.key;
        this.resource = builder.resource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getResource()
    {
        return resource;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String resource;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder resource( final String resource )
        {
            this.resource = resource;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( resource, "resource is required" );
        }

        public UpdateStylesParams build()
        {
            validate();
            return new UpdateStylesParams( this );
        }
    }
}


