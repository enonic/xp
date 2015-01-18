package com.enonic.wem.api.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;
import com.enonic.wem.api.util.BinaryReference;

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
        final Set<AttachedBinary> returnFields = Sets.newHashSet();
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
        private final Set<AttachedBinary> nodeAttachedBinaries = Sets.newHashSet();

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
