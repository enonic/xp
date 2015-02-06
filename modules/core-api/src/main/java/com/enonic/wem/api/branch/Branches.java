package com.enonic.wem.api.branch;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class Branches
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

    public static Branches empty()
    {
        ImmutableSet<Branch> empty = ImmutableSet.of();
        return new Branches( empty );
    }

}
