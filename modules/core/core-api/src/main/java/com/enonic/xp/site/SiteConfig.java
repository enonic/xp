package com.enonic.xp.site;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public final class SiteConfig
{
    private final ApplicationKey applicationKey;

    private final PropertyTree config;

    public SiteConfig( final Builder builder )
    {
        Preconditions.checkNotNull( builder.applicationKey, "applicationKey cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.applicationKey = builder.applicationKey;
        this.config = builder.config;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final SiteConfig that = (SiteConfig) o;

        return Objects.equals( this.applicationKey, that.applicationKey ) && Objects.equals( this.config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, config );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ApplicationKey applicationKey;

        private PropertyTree config;

        public Builder application( ApplicationKey value )
        {
            this.applicationKey = value;
            return this;
        }

        public Builder config( PropertyTree value )
        {
            this.config = value;
            return this;
        }

        public SiteConfig build()
        {
            return new SiteConfig( this );
        }
    }
}
