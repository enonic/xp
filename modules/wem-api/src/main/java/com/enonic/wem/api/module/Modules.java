package com.enonic.wem.api.module;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

@Immutable
public final class Modules
    extends AbstractImmutableEntityList<Module>
{
    private final ImmutableMap<ModuleKey, Module> map;

    private Modules( final ImmutableList<Module> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public ModuleKeys getModuleKeys()
    {
        return ModuleKeys.from( map.keySet() );
    }

    public Module getModule( final ModuleKey ModuleKey )
    {
        return map.get( ModuleKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Modules empty()
    {
        final ImmutableList<Module> list = ImmutableList.of();
        return new Modules( list );
    }

    public static Modules from( final Module... modules )
    {
        return new Modules( ImmutableList.copyOf( modules ) );
    }

    public static Modules from( final Iterable<? extends Module> modules )
    {
        return new Modules( ImmutableList.copyOf( modules ) );
    }

    public static Modules from( final Collection<? extends Module> modules )
    {
        return new Modules( ImmutableList.copyOf( modules ) );
    }

    private final static class ToKeyFunction
        implements Function<Module, ModuleKey>
    {
        @Override
        public ModuleKey apply( final Module value )
        {
            return value.getKey();
        }
    }


}
