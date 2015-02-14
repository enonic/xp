package com.enonic.xp.admin.impl.rest.resource.blob;

import com.enonic.xp.core.blob.BlobKey;

final class UploadItem
{
    private final BlobKey blobKey;

    private final String name;

    private final long size;

    private final String mimeType;

    public UploadItem( final Builder builder )
    {
        this.name = builder.name;
        this.size = builder.size;
        this.mimeType = builder.mimeType;
        this.blobKey = builder.blobKey;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public String getName()
    {
        return this.name;
    }

    public long getSize()
    {
        return this.size;
    }

    public String getMimeType()
    {
        return this.mimeType;
    }

    public static Builder newUploadItem()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private long size;

        private String mimeType;

        private BlobKey blobKey;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder size( final long size )
        {
            this.size = size;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder blobKey( final BlobKey blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }

        public UploadItem build()
        {

            return new UploadItem( this );
        }

    }
}
