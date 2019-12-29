package com.enonic.xp.blob;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class BlobKeys
    extends AbstractImmutableEntitySet<BlobKey>
{
    private BlobKeys( final ImmutableSet<BlobKey> set )
    {
        super( set );
    }

    public static BlobKeys empty()
    {
        return new BlobKeys( ImmutableSet.of() );
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
        final Set<BlobKey> blobKeys = new LinkedHashSet<>();

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
