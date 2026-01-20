package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class BinaryAttachments
    extends AbstractImmutableEntityList<BinaryAttachment>
{
    private static final BinaryAttachments EMPTY = new BinaryAttachments( ImmutableList.of() );

    public static BinaryAttachments empty()
    {
        return EMPTY;
    }

    private BinaryAttachments( final ImmutableList<BinaryAttachment> list )
    {
        super( list );
    }

    public BinaryAttachment get( final BinaryReference binaryReference )
    {
        return stream().filter( ba -> ba.getReference().equals( binaryReference ) ).findFirst().orElse( null );
    }

    public static Collector<BinaryAttachment, ?, BinaryAttachments> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), BinaryAttachments::fromInternal );
    }

    private static BinaryAttachments fromInternal( final ImmutableList<BinaryAttachment> list )
    {
        return list.isEmpty() ? EMPTY : new BinaryAttachments( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<BinaryAttachment> binaryAttachments = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final BinaryAttachment binaryAttachment )
        {
            this.binaryAttachments.add( binaryAttachment );
            return this;
        }

        public Builder addAll( final Iterable<BinaryAttachment> binaryAttachments )
        {
            this.binaryAttachments.addAll( binaryAttachments );
            return this;
        }

        public BinaryAttachments build()
        {
            return fromInternal( binaryAttachments.build() );
        }
    }
}
