package com.enonic.xp.node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public class AttachedBinaries
    extends AbstractImmutableEntitySet<AttachedBinary>
{
    private AttachedBinaries( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.nodeAttachedBinaries ) );
    }

    private AttachedBinaries( final ImmutableSet<AttachedBinary> set )
    {
        super( set );
    }

    private AttachedBinaries( final Set<AttachedBinary> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static AttachedBinaries empty()
    {
        final Set<AttachedBinary> returnFields = new HashSet<>();
        return new AttachedBinaries( returnFields );
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
        return new AttachedBinaries( ImmutableSet.copyOf( references ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<AttachedBinary> nodeAttachedBinaries = new HashSet<>();

        public Builder add( final AttachedBinary attachedBinary )
        {
            this.nodeAttachedBinaries.add( attachedBinary );
            return this;
        }

        public Set<AttachedBinary> getNodeAttachedBinaries()
        {
            return nodeAttachedBinaries;
        }

        public AttachedBinaries build()
        {
            return new AttachedBinaries( this );
        }
    }

}
