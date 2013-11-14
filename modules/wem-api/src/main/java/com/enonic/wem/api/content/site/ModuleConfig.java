package com.enonic.wem.api.content.site;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;

public final class ModuleConfig
{
    private final ModuleKey module;

    private final RootDataSet config;

    public ModuleConfig( final Builder builder )
    {
        Preconditions.checkNotNull( builder.module, "module cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.module = builder.module;
        this.config = builder.config;
    }

    public ModuleKey getModule()
    {
        return module;
    }

    public RootDataSet getConfig()
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

        final ModuleConfig that = (ModuleConfig) o;

        return Objects.equals( this.module, that.module ) && Objects.equals( this.config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( module, config );
    }

    public static Builder newModuleConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ModuleKey module;

        private RootDataSet config;

        public Builder module( ModuleKey value )
        {
            this.module = value;
            return this;
        }

        public Builder config( RootDataSet value )
        {
            this.config = value;
            return this;
        }

        public ModuleConfig build()
        {
            return new ModuleConfig( this );
        }
    }
}
