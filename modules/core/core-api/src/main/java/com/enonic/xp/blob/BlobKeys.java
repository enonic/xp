package com.enonic.xp.blob;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class BlobKeys
    extends AbstractImmutableEntitySet<BlobKey>
{
    private BlobKeys( final ImmutableSet<BlobKey> set )
    {
        super( set );
    }

    public static BlobKeys empty()
    {
        return new BlobKeys( ImmutableSet.<BlobKey>of() );
    }

    public static BlobKeys from( final BlobKey... blobKeys )
    {
        return new BlobKeys( ImmutableSet.copyOf( blobKeys ) );
    }


    public static BlobKeys from( final Collection<BlobKey> blobKeys )
    {
        return new BlobKeys( ImmutableSet.copyOf( blobKeys ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<BlobKey> blobKeys = Sets.newLinkedHashSet();

        public Builder add( final BlobKey blobKey )
        {
            this.blobKeys.add( blobKey );
            return this;
        }

        public Builder addAll( final Collection<BlobKey> blobKeys )
        {
            this.blobKeys.addAll( blobKeys );
            return this;
        }


        public BlobKeys build()
        {
            return new BlobKeys( ImmutableSet.copyOf( this.blobKeys ) );
        }

    }

}
