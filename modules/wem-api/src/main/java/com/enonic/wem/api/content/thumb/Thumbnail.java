package com.enonic.wem.api.content.thumb;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.util.BinaryReference;

public final class Thumbnail
{
    private final BinaryReference binaryReference;

    private final String mimeType;

    private final long size;

    private Thumbnail( final BinaryReference binaryReference, final String mimeType, final Long size )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( binaryReference, "binaryReference is mandatory" );
        Preconditions.checkNotNull( size, "size is mandatory" );
        this.binaryReference = binaryReference;
        this.mimeType = mimeType;
        this.size = size;
    }

    // TODO: Rename to getBinaryReference
    public BinaryReference getBinaryReference()
    {
        return binaryReference;
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
        if ( binaryReference != null ? !binaryReference.equals( thumbnail.binaryReference ) : thumbnail.binaryReference != null )
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
        int result = binaryReference != null ? binaryReference.hashCode() : 0;
        result = 31 * result + ( mimeType != null ? mimeType.hashCode() : 0 );
        result = 31 * result + (int) ( size ^ ( size >>> 32 ) );
        return result;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "mimeType", mimeType ).
            add( "binaryReference", binaryReference ).
            add( "size", size ).
            toString();
    }

    public static Thumbnail from( final BinaryReference binaryReference, final String mimeType, final long size )
    {
        return new Thumbnail( binaryReference, mimeType, size );
    }
}
