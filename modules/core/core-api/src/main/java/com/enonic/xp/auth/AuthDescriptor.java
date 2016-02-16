package com.enonic.xp.auth;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;

public class AuthDescriptor
{
    private final ApplicationKey key;

    private final Form config;

    private AuthDescriptor( final Builder builder )
    {
        key = builder.key;
        config = builder.config;
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public Form getConfig()
    {
        return config;
    }

    public ResourceKey getResourceKey()
    {
        return ResourceKey.from( key, "auth/auth.js" );
    }

    public static ResourceKey toResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "auth/auth.xml" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private Form config;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public AuthDescriptor build()
        {
            return new AuthDescriptor( this );
        }
    }
}
