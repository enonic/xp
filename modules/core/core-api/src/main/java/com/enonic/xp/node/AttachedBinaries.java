package com.enonic.xp.node;

import java.util.Collection;

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
        for ( final AttachedBinary attachedBinary : this.set )
        {
            if ( attachedBinary.getBinaryReference().equals( reference ) )
            {
                return attachedBinary;
            }
        }

        return null;
    }

    public static AttachedBinaries fromCollection( final Collection<AttachedBinary> references )
    {
        return fromInternal( ImmutableSet.copyOf( references ) );
    }

    private static AttachedBinaries fromInternal( final ImmutableSet<AttachedBinary> set )
    {
        return set.isEmpty() ? EMPTY : new AttachedBinaries( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<AttachedBinary> nodeAttachedBinaries = ImmutableSet.builder();

        public Builder add( final AttachedBinary attachedBinary )
        {
            this.nodeAttachedBinaries.add( attachedBinary );
            return this;
        }

        public AttachedBinaries build()
        {
            return fromInternal( nodeAttachedBinaries.build() );
        }
    }

}
