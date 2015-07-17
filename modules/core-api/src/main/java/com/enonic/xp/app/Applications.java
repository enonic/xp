package com.enonic.xp.app;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.module.Module;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class Applications
    extends AbstractImmutableEntityList<Module>
{
    private final ImmutableMap<ApplicationKey, Module> map;

    private Applications( final ImmutableList<Module> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public ApplicationKeys getApplicationKeys()
    {
        return ApplicationKeys.from( map.keySet() );
    }

    public Module getModule( final ApplicationKey ApplicationKey )
    {
        return map.get( ApplicationKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Applications empty()
    {
        final ImmutableList<Module> list = ImmutableList.of();
        return new Applications( list );
    }

    public static Applications from( final Module... modules )
    {
        return new Applications( ImmutableList.copyOf( modules ) );
    }

    public static Applications from( final Iterable<? extends Module> modules )
    {
        return new Applications( ImmutableList.copyOf( modules ) );
    }

    public static Applications from( final Collection<? extends Module> modules )
    {
        return new Applications( ImmutableList.copyOf( modules ) );
    }

    private final static class ToKeyFunction
        implements Function<Module, ApplicationKey>
    {
        @Override
        public ApplicationKey apply( final Module value )
        {
            return value.getKey();
        }
    }


}
