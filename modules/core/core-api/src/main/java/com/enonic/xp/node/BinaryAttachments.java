package com.enonic.xp.node;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;
import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class BinaryAttachments
    extends AbstractImmutableEntityList<BinaryAttachment>
{
    private static final BinaryAttachments EMPTY = new BinaryAttachments( ImmutableList.of() );

    private BinaryAttachments( final ImmutableList<BinaryAttachment> list )
    {
        super( list );
    }

    public BinaryAttachment get( final BinaryReference binaryReference )
    {
        return this.list.stream().filter( ba -> ba.getReference().equals( binaryReference ) ).findAny().orElse( null );
    }

    private static BinaryAttachments fromInternal( final ImmutableList<BinaryAttachment> list )
    {
        return list.isEmpty() ? EMPTY : new BinaryAttachments( list );
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
        private final ImmutableList.Builder<BinaryAttachment> binaryAttachments = ImmutableList.builder();

        public Builder add( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public BinaryAttachments build()
        {
            return fromInternal( binaryAttachments.build() );
        }
    }
}
