package com.enonic.xp.app;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.icon.Icon;

@PublicApi
public final class ApplicationDescriptor
{
    private final ApplicationKey key;

    private final String description;

    private final Icon icon;

    private ApplicationDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key is required" );
        this.key = builder.key;
        this.description = builder.description != null ? builder.description : "";
        this.icon = builder.icon;
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, description, icon );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String description;

        private Icon icon;

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

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public ApplicationDescriptor build()
        {
            return new ApplicationDescriptor( this );
        }
    }
}
