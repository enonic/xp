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
    private static final BinaryAttachments EMPTY = new BinaryAttachments( ImmutableSet.of(), false );

    private BinaryAttachments( final ImmutableSet<BinaryAttachment> set, boolean ignore )
    {
        super( set );
    }

    public BinaryAttachment get( final BinaryReference binaryReference )
    {
        return this.set.stream().filter( ba -> ba.getReference().equals( binaryReference ) ).findAny().orElse( null );
    }

    private static BinaryAttachments fromInternal( final ImmutableSet<BinaryAttachment> set )
    {
        return set.isEmpty() ? EMPTY : new BinaryAttachments( set, false );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static BinaryAttachments empty()
    {
        return EMPTY;
    }

    public static final class Builder
    {
        private final Set<BinaryAttachment> binaryAttachments = new HashSet<>();

        public Builder add( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public BinaryAttachments build()
        {
            return fromInternal( ImmutableSet.copyOf( binaryAttachments ) );
        }
    }
}
