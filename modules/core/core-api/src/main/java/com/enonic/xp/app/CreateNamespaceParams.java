package com.enonic.xp.app;

import static java.util.Objects.requireNonNull;


public final class CreateNamespaceParams
{
    private final ApplicationKey key;

    private final String description;

    private CreateNamespaceParams( final Builder builder )
    {
        this.key = builder.key;
        this.description = builder.description;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String description;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public CreateNamespaceParams build()
        {
            validate();
            return new CreateNamespaceParams( this );
        }
    }
}