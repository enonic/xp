package com.enonic.wem.api.node;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;
import com.enonic.wem.api.util.BinaryReference;

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

    public static BinaryAttachments empty()
    {
        final Set<BinaryAttachment> returnFields = Sets.newHashSet();
        return new BinaryAttachments( returnFields );
    }
}
