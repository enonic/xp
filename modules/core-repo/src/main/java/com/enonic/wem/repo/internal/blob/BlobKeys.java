package com.enonic.wem.repo.internal.blob;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class BlobKeys
    implements Iterable<BlobKey>
{
    private final ImmutableSet<BlobKey> blobKeys;

    private BlobKeys( final Builder builder )
    {
        this.blobKeys = ImmutableSet.copyOf( builder.blobKeys );
    }

    private BlobKeys( final ImmutableSet<BlobKey> set )
    {
        this.blobKeys = set;
    }

    public static BlobKeys empty()
    {
        final ImmutableSet<BlobKey> emptySet = ImmutableSet.of();
        return new BlobKeys( emptySet );
    }


    public static Builder create()
    {
        return new Builder();
    }

    public int size()
    {
        return blobKeys.size();
    }

    @Override
    public Iterator<BlobKey> iterator()
    {
        return blobKeys.iterator();
    }

    public static class Builder
    {
        private final Set<BlobKey> blobKeys = Sets.newLinkedHashSet();

        public Builder add( final BlobKey blobKey )
        {
            this.blobKeys.add( blobKey );
            return this;
        }

        public BlobKeys build()
        {
            return new BlobKeys( this );
        }

    }


}
