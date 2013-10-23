package com.enonic.wem.api.module;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

@Immutable
public final class Modules
    extends AbstractImmutableEntityList<Module>
{
    private final ImmutableMap<ModuleName, Module> map;

    private Modules( final ImmutableList<Module> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<ModuleName> getNames()
    {
        return map.keySet();
    }

    public Module getModule( final ModuleName moduleName )
    {
        return map.get( moduleName );
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

    private final static class ToNameFunction
        implements Function<Module, ModuleName>
    {
        @Override
        public ModuleName apply( final Module value )
        {
            return value.getName();
        }
    }


}
