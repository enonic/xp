package com.enonic.xp.s3;


import com.enonic.xp.blob.BlobStore;

public abstract class AbstractReadThroughBlobStore
    implements BlobStore
{
    protected final BlobStore localStore;

    protected AbstractReadThroughBlobStore( final Builder builder )
    {
        localStore = builder.localStore;
    }

    public static class Builder<B extends Builder>
    {
        private BlobStore localStore;

        public B localStore( BlobStore localStore )
        {
            this.localStore = localStore;
            return (B) this;
        }

    }
}
