package com.enonic.xp.blob;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class BlobKeys
    extends AbstractImmutableEntitySet<BlobKey>
{
    private static final BlobKeys EMPTY = new BlobKeys( ImmutableSet.of() );

    private BlobKeys( final ImmutableSet<BlobKey> set )
    {
        super( set );
    }

    public static BlobKeys empty()
    {
        return EMPTY;
    }

    public static BlobKeys from( final BlobKey... blobKeys )
    {
        return fromInternal( ImmutableSet.copyOf( blobKeys ) );
    }

    public static BlobKeys from( final Iterable<? extends BlobKey> blobKeys )
    {
        return fromInternal( ImmutableSet.copyOf( blobKeys ) );
    }

    public static Collector<BlobKey, ?, BlobKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), BlobKeys::fromInternal );
    }

    private static BlobKeys fromInternal( final ImmutableSet<BlobKey> set )
    {
        return set.isEmpty() ? EMPTY : new BlobKeys( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final ImmutableSet.Builder<BlobKey> blobKeys = ImmutableSet.builder();

        public Builder add( final BlobKey blobKey )
        {
            this.blobKeys.add( blobKey );
            return this;
        }

        public Builder addAll( final Iterable<? extends BlobKey> blobKeys )
        {
            this.blobKeys.addAll( blobKeys );
            return this;
        }

        public BlobKeys build()
        {
            return new BlobKeys( this.blobKeys.build() );
        }
    }
}
