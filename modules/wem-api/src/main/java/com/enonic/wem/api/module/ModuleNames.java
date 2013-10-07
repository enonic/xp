package com.enonic.wem.api.module;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ModuleNames
    extends AbstractImmutableEntityList<ModuleName>
{
    private ModuleNames( final ImmutableList<ModuleName> list )
    {
        super( list );
    }

    public static ModuleNames from( final ModuleName... moduleNames )
    {
        return new ModuleNames( ImmutableList.copyOf( moduleNames ) );
    }

    public static ModuleNames from( final Iterable<? extends ModuleName> moduleNames )
    {
        return new ModuleNames( ImmutableList.copyOf( moduleNames ) );
    }

    public static ModuleNames from( final Collection<? extends ModuleName> moduleNames )
    {
        return new ModuleNames( ImmutableList.copyOf( moduleNames ) );
    }
}
