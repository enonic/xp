package com.enonic.wem.api.module;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public final class ModuleKeyResolver
{
    private final static ModuleKeyResolver EMPTY = new ModuleKeyResolver();

    private final ImmutableMap<ModuleName, ModuleKey> map;

    private ModuleKeyResolver()
    {
        this.map = ImmutableMap.of();
    }

    private ModuleKeyResolver( final Iterable<ModuleKey> keys )
    {
        final ImmutableMap.Builder<ModuleName, ModuleKey> builder = ImmutableMap.builder();
        for ( final ModuleKey key : keys )
        {
            builder.put( key.getName(), key );
        }

        this.map = builder.build();
    }

    public ModuleKey resolve( final ModuleName name )
        throws ModuleNotFoundException
    {
        if ( name.isSystem() )
        {
            return ModuleKey.SYSTEM;
        }

        final ModuleKey key = this.map.get( name );
        if ( key != null )
        {
            return key;
        }

        throw new ModuleNotFoundException( name );
    }

    public static ModuleKeyResolver empty()
    {
        return EMPTY;
    }

    public static ModuleKeyResolver from( final ModuleKey... keys )
    {
        return new ModuleKeyResolver( Lists.newArrayList( keys ) );
    }

    public static ModuleKeyResolver from( final Iterable<ModuleKey> keys )
    {
        return new ModuleKeyResolver( keys );
    }
}
