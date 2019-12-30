package com.enonic.xp.branch;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
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

    public static Branches from( final Iterable<Branch> branches )
    {
        return new Branches( ImmutableSet.copyOf( branches ) );
    }

    public static Branches empty()
    {
        return new Branches( ImmutableSet.of() );
    }
}
