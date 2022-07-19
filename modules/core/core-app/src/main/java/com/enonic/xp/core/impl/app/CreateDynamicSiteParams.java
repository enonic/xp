package com.enonic.xp.core.impl.app;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;

public final class CreateDynamicSiteParams
{
    private final ApplicationKey key;

    private final String resource;

    private CreateDynamicSiteParams( final Builder builder )
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
            Preconditions.checkNotNull( key, "key must be set" );
            Preconditions.checkNotNull( resource, "resource must be set" );
        }

        public CreateDynamicSiteParams build()
        {
            validate();
            return new CreateDynamicSiteParams( this );
        }
    }
}


