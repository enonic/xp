package com.enonic.xp.node;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public class BinaryAttachments
    extends AbstractImmutableEntitySet<BinaryAttachment>
{
    public BinaryAttachments( final ImmutableSet<BinaryAttachment> set )
    {
        super( set );
    }

    public BinaryAttachment get( final BinaryReference binaryReference )
    {
        for ( final BinaryAttachment binaryAttachment : this.set )
        {
            if ( binaryAttachment.getReference().equals( binaryReference ) )
            {
                return binaryAttachment;
            }
        }

        return null;
    }

    private BinaryAttachments( final Set<BinaryAttachment> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static BinaryAttachments empty()
    {
        final Set<BinaryAttachment> returnFields = new HashSet<>();
        return new BinaryAttachments( returnFields );
    }

    public static final class Builder
    {
        private Set<BinaryAttachment> binaryAttachments = new HashSet<>();

        public Builder add( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public BinaryAttachments build()
        {
            return new BinaryAttachments( ImmutableSet.copyOf( this.binaryAttachments ) );
        }
    }
}
