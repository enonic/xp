package com.enonic.xp.security;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public class CreateServiceAccountParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String description;

    private final PropertyTree data;

    private CreateServiceAccountParams( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.data = builder.data;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateServiceAccountParams params )
    {
        return new Builder( params );
    }

    public static class Builder
    {
        private PrincipalKey key;

        private String displayName;

        private String description;

        private PropertyTree data;

        private Builder()
        {
        }

        private Builder( final CreateServiceAccountParams params )
        {
            this.key = params.key;
            this.displayName = params.displayName;
            this.description = params.description;
            this.data = params.data;
        }

        public Builder key( final PrincipalKey key )
        {
            this.key = key;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( key, "key is required" );
            Objects.requireNonNull( displayName, "displayName is required" );
            if ( !key.isServiceAccount() )
            {
                throw new IllegalArgumentException( "Invalid PrincipalType for ServiceAccount key: " + key.getType() );
            }
        }

        public CreateServiceAccountParams build()
        {
            validate();

            return new CreateServiceAccountParams( this );
        }
    }
}
