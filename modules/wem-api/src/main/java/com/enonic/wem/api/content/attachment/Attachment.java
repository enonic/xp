package com.enonic.wem.api.content.attachment;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;

public final class Attachment
{
    private final BlobKey blobKey;

    private final long size;

    private final String mimeType;

    private final String name;

    private final String label;

    public Attachment( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is mandatory for an Attachment" );
        Preconditions.checkNotNull( builder.mimeType, "mimeType is mandatory for an Attachment" );
        Preconditions.checkNotNull( builder.blobKey, "blobKey is mandatory for an Attachment" );
        Preconditions.checkNotNull( builder.size, "size is mandatory for an Attachment" );

        this.blobKey = builder.blobKey;
        this.mimeType = builder.mimeType;
        this.name = builder.name;
        this.size = builder.size;
        this.label = builder.label;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public long getSize()
    {
        return size;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Attachment ) )
        {
            return false;
        }
        final Attachment that = (Attachment) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.mimeType, that.mimeType ) &&
            Objects.equal( this.label, that.label ) &&
            Objects.equal( this.blobKey, that.blobKey ) &&
            Objects.equal( this.size, that.size );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, mimeType, label, size, blobKey );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", name );
        s.add( "mimeType", mimeType );
        s.add( "label", label );
        s.add( "size", size );
        s.add( "blobKey", blobKey );
        return s.toString();
    }

    public static Builder newAttachment()
    {
        return new Builder();
    }

    public static Builder newAttachment( final Attachment source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private BlobKey blobKey;

        private String mimeType;

        private String name;

        private String label;

        private long size;

        private Builder()
        {

        }

        private Builder( final Attachment attachment )
        {
            this.blobKey = attachment.blobKey;
            this.mimeType = attachment.mimeType;
            this.name = attachment.name;
            this.label = attachment.label;
        }

        public Builder blobKey( final BlobKey blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder size( final long size )
        {
            this.size = size;
            return this;
        }

        public Attachment build()
        {
            return new Attachment( this );
        }
    }
}
