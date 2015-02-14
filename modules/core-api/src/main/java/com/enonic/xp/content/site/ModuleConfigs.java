package com.enonic.xp.content.site;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class ModuleConfigs
    extends AbstractImmutableEntityList<ModuleConfig>
{
    private final ImmutableMap<ModuleKey, ModuleConfig> modulesByName;

    private ModuleConfigs( final ImmutableList<ModuleConfig> list )
    {
        super( list );
        this.modulesByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ModuleConfig get( final ModuleKey moduleKey )
    {
        return this.modulesByName.get( moduleKey );
    }

    public ModuleConfig get( final String moduleKey )
    {
        return get( ModuleKey.from( moduleKey ) );
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

    private final static class ToNameFunction
        implements Function<ModuleConfig, ModuleKey>
    {
        @Override
        public ModuleKey apply( final ModuleConfig value )
        {
            return value.getModule();
        }
    }
}
