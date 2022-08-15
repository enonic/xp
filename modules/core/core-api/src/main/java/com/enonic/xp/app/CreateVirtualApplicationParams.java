package com.enonic.xp.app;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class CreateVirtualApplicationParams
{
    private final ApplicationKey key;

    private CreateVirtualApplicationParams( final Builder builder )
    {
        this.key = builder.key;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( key, "key must be set" );
        }

        public CreateVirtualApplicationParams build()
        {
            validate();
            return new CreateVirtualApplicationParams( this );
        }
    }
}
