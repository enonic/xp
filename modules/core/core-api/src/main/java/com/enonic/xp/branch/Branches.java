package com.enonic.xp.branch;

import java.util.stream.Collector;

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

    public static Collector<Branch, ?, Branches> collecting()
    {
        return Collector.of( ImmutableSet.Builder<Branch>::new, ImmutableSet.Builder::add, ( left, right ) -> left.addAll( right.build() ),
                             is -> new Branches( is.build() ) );
    }
}
