package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@Beta
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
        final Set<BinaryAttachment> returnFields = Sets.newHashSet();
        return new BinaryAttachments( returnFields );
    }

    public static final class Builder
    {
        private Set<BinaryAttachment> binaryAttachments = Sets.newHashSet();

        public Builder add( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public Builder add( final BinaryAttachments binaryAttachments )
        {
            if ( binaryAttachments != null )
            {

                this.binaryAttachments.addAll( binaryAttachments.getSet() );
            }
            return this;
        }

        public BinaryAttachments build()
        {
            return new BinaryAttachments( ImmutableSet.copyOf( this.binaryAttachments ) );
        }
    }
}
