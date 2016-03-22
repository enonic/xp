package com.enonic.xp.macro;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;

@Beta
public class MacroDescriptor
{
    private final static String SITE_MACROS_PREFIX = "site/macros/";

    private final MacroKey key;

    private MacroDescriptor( final Builder builder ) {
        this.key = builder.key;
    }

    public MacroKey getKey()
    {
        return key;
    }

    public ResourceKey toResourceKey( )
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private MacroKey key;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
            return this;
        }

        public MacroDescriptor build()
        {
            return new MacroDescriptor( this );
        }
    }

}
