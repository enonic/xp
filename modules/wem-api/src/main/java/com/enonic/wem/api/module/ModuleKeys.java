package com.enonic.wem.api.module;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ModuleKeys
    extends AbstractImmutableEntityList<ModuleKey>
{
    private ModuleKeys( final ImmutableList<ModuleKey> list )
    {
        super( list );
    }

    public static ModuleKeys from( final ModuleKey... moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final Iterable<? extends ModuleKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final Collection<? extends ModuleKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }
}
