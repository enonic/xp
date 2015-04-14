package com.enonic.xp.content.site;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.Content;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;

@Beta
public final class Site
    extends Content
{
    public Site( final Builder builder )
    {
        super( builder );
    }

    public String getDescription()
    {
        return this.getData().getString( "description" );
    }

    public PropertyTree getModuleConfig( final ModuleKey moduleKey )
    {
        final ModuleConfig moduleConfig = this.getModuleConfigs().get( moduleKey );
        if ( moduleConfig == null )
        {
            return null;
        }
        return moduleConfig.getConfig();
    }

    public ModuleConfigs getModuleConfigs()
    {
        return new ModuleConfigsDataSerializer().fromProperties( this.getData().getRoot() ).build();
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

        return super.equals( o );
    }

    public static Builder newSite()
    {
        return new Builder();
    }

    public static Builder newSite( final Site source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends Content.Builder<Builder, Site>
    {
        private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        public Builder description( final String description )
        {

            if ( data == null )
            {
                data = new PropertyTree();
            }
            data.setString( "description", description );
            return this;
        }

        public Builder addModuleConfig( final ModuleConfig moduleConfig )
        {
            if ( data == null )
            {
                data = new PropertyTree();
            }
            MODULE_CONFIGS_DATA_SERIALIZER.toProperties( moduleConfig, data.getRoot() );

            return this;
        }

        public Builder moduleConfigs( final ModuleConfigs moduleConfigs )
        {
            if ( data == null )
            {
                data = new PropertyTree();
            }
            MODULE_CONFIGS_DATA_SERIALIZER.toProperties( moduleConfigs, data.getRoot() );
            return this;
        }

        @Override
        public Site build()
        {
            return new Site( this );
        }

    }
}
