package com.enonic.wem.api.content.thumb;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;

public final class Thumbnail
{
    private final BlobKey blobKey;

    private final String mimeType;

    private final long size;

    private Thumbnail( final BlobKey blobKey, final String mimeType, final Long size )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( blobKey, "blobKey is mandatory" );
        Preconditions.checkNotNull( blobKey, "size is mandatory" );
        this.blobKey = blobKey;
        this.mimeType = mimeType;
        this.size = size;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public String getMimeType()
    {
        return mimeType;
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Thumbnail thumbnail = (Thumbnail) o;

        if ( size != thumbnail.size )
        {
            return false;
        }
        if ( blobKey != null ? !blobKey.equals( thumbnail.blobKey ) : thumbnail.blobKey != null )
        {
            return false;
        }
        if ( mimeType != null ? !mimeType.equals( thumbnail.mimeType ) : thumbnail.mimeType != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = blobKey != null ? blobKey.hashCode() : 0;
        result = 31 * result + ( mimeType != null ? mimeType.hashCode() : 0 );
        result = 31 * result + (int) ( size ^ ( size >>> 32 ) );
        return result;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "mimeType", mimeType ).
            add( "blobKey", blobKey ).
            add( "size", size ).
            toString();
    }

    public static Thumbnail from( final BlobKey blobKey, final String mimeType, final long size )
    {
        return new Thumbnail( blobKey, mimeType, size );
    }
}
