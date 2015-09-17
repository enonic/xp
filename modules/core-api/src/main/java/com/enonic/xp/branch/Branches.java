package com.enonic.xp.branch;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class Branches
    extends AbstractImmutableEntitySet<Branch>
{
    private Branches( final ImmutableSet<Branch> set )
    {
        super( set );
    }

    public static Branches from( final Branch... branches )
    {
        return new Branches( ImmutableSet.copyOf( branches ) );
    }

    public static Branches from( final Collection<Branch> branches )
    {
        return new Branches( ImmutableSet.copyOf( branches ) );
    }

    public static Branches empty()
    {
        return new Branches( ImmutableSet.of() );
    }
}
