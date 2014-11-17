package com.enonic.wem.api.node;


import com.enonic.wem.api.blob.BlobKey;

public final class Attachment
{
    private final String name;

    private final long size;

    private final String mimeType;

    private final BlobKey blobKey;

    private Attachment( final Builder builder )
    {
        this.name = builder.name;
        this.size = builder.size;
        this.mimeType = builder.mimeType;
        this.blobKey = builder.blobKey;
    }

    public String name()
    {
        return name;
    }

    public long size()
    {
        return size;
    }

    public String mimeType()
    {
        return mimeType;
    }

    public BlobKey blobKey()
    {
        return blobKey;
    }

    public static Builder newAttachment()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String name;

        private long size;

        private String mimeType;

        private BlobKey blobKey;

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder size( final long value )
        {
            this.size = value;
            return this;
        }

        public Builder mimeType( final String value )
        {
            this.mimeType = value;
            return this;
        }

        public Builder blobKey( final BlobKey value )
        {
            this.blobKey = value;
            return this;
        }

        public Attachment build()
        {
            return new Attachment( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Attachment that = (Attachment) o;

        if ( size != that.size )
        {
            return false;
        }
        if ( blobKey != null ? !blobKey.equals( that.blobKey ) : that.blobKey != null )
        {
            return false;
        }
        if ( mimeType != null ? !mimeType.equals( that.mimeType ) : that.mimeType != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) ( size ^ ( size >>> 32 ) );
        result = 31 * result + ( mimeType != null ? mimeType.hashCode() : 0 );
        result = 31 * result + ( blobKey != null ? blobKey.hashCode() : 0 );
        return result;
    }
}
