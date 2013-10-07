package com.enonic.wem.api.module;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ModuleVersions
    extends AbstractImmutableEntityList<ModuleVersion>
{
    private ModuleVersions( final ImmutableList<ModuleVersion> list )
    {
        super( list );
    }

    public static ModuleVersions from( final ModuleVersion... moduleVersions )
    {
        return new ModuleVersions( ImmutableList.copyOf( moduleVersions ) );
    }

    public static ModuleVersions from( final Iterable<? extends ModuleVersion> moduleVersions )
    {
        return new ModuleVersions( ImmutableList.copyOf( moduleVersions ) );
    }

    public static ModuleVersions from( final Collection<? extends ModuleVersion> moduleVersions )
    {
        return new ModuleVersions( ImmutableList.copyOf( moduleVersions ) );
    }
}
