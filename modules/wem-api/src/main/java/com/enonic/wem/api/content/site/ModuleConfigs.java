package com.enonic.wem.api.content.site;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class ModuleConfigs
    extends AbstractImmutableEntityList<ModuleConfig>
{
    private ModuleConfigs( final ImmutableList<ModuleConfig> list )
    {
        super( list );
    }

    public static ModuleConfigs empty()
    {
        final ImmutableList<ModuleConfig> list = ImmutableList.of();
        return new ModuleConfigs( list );
    }

    public static ModuleConfigs from( final ModuleConfig... moduleConfigs )
    {
        return new ModuleConfigs( ImmutableList.copyOf( moduleConfigs ) );
    }

    public static ModuleConfigs from( final Iterable<? extends ModuleConfig> moduleConfigs )
    {
        return new ModuleConfigs( ImmutableList.copyOf( moduleConfigs ) );
    }

    public static ModuleConfigs from( final Collection<? extends ModuleConfig> moduleConfigs )
    {
        return new ModuleConfigs( ImmutableList.copyOf( moduleConfigs ) );
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ModuleConfig> builder = ImmutableList.builder();

        public Builder add( ModuleConfig moduleConfig )
        {
            builder.add( moduleConfig );
            return this;
        }

        public ModuleConfigs build()
        {
            return new ModuleConfigs( builder.build() );
        }
    }
}
