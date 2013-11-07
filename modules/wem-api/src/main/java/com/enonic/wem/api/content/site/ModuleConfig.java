package com.enonic.wem.api.content.site;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;

public class ModuleConfig
{
    private final ModuleKey module;

    private final RootDataSet config;

    public ModuleConfig( final Builder builder )
    {
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
