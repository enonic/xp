package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class AttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private static final AttachedBinaries EMPTY = new AttachedBinaries( ImmutableSet.of() );

    private AttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
    }

    public static AttachedBinaries empty()
    {
        return EMPTY;
    }

    public AttachedBinary getByBinaryReference( final BinaryReference reference )
    {
        return this.set.stream()
            .filter( attachedBinary -> reference.equals( attachedBinary.getBinaryReference() ) )
            .findAny()
            .orElse( null );
    }

    public static AttachedBinaries from( final Iterable<? extends AttachedBinary> references )
    {
        return fromInternal( ImmutableSet.copyOf( references ) );
    }

    public static Collector<AttachedBinary, ?, AttachedBinaries> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), AttachedBinaries::fromInternal );
    }

    private static AttachedBinaries fromInternal( final ImmutableSet<AttachedBinary> set )
    {
        return set.isEmpty() ? EMPTY : new AttachedBinaries( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<AttachedBinary> nodeAttachedBinaries = ImmutableSet.builder();

        public Builder add( final AttachedBinary attachedBinary )
        {
            this.nodeAttachedBinaries.add( attachedBinary );
            return this;
        }

        public Builder addAll( final Iterable<AttachedBinary> attachedBinaries )
        {
            this.nodeAttachedBinaries.addAll( attachedBinaries );
            return this;
        }

        public AttachedBinaries build()
        {
            return fromInternal( nodeAttachedBinaries.build() );
        }
    }
}
