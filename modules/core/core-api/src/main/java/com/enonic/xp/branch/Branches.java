package com.enonic.xp.branch;

import java.util.Arrays;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class Branches
    extends AbstractImmutableEntitySet<Branch>
{
    private Branches( final Builder builder )
    {
        super( builder.branches.build() );
    }

    public static Branches from( final Branch... branches )
    {
        return Branches.create().addAll( branches ).build();
    }

    public static Branches from( final Iterable<Branch> branches )
    {
        return Branches.create().addAll( branches ).build();
    }

    public static Branches empty()
    {
        return Branches.create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        public Builder add( final Branch branch )
        {
            this.branches.add( branch );
            return this;
        }

        public Builder addAll( final Iterable<Branch> branches )
        {
            this.branches.addAll( branches );
            return this;
        }

        public Builder addAll( final Branch... branches )
        {
            this.branches.addAll( Arrays.asList( branches ) );
            return this;
        }

        public Branches build()
        {
            return new Branches( this );
        }
    }
}
