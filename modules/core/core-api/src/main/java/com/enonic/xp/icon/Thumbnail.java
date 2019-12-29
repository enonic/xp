package com.enonic.xp.icon;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class Thumbnail
{
    private final BinaryReference binaryReference;

    private final String mimeType;

    private final long size;

    private Thumbnail( final BinaryReference binaryReference, final String mimeType, final Long size )
    {
        Preconditions.checkNotNull( binaryReference, "binaryReference is mandatory" );
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( size, "size is mandatory" );
        this.binaryReference = binaryReference;
        this.mimeType = mimeType;
        this.size = size;
    }

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

        final Thumbnail other = (Thumbnail) o;

        return Objects.equals( binaryReference, other.binaryReference ) &&
            Objects.equals( mimeType, other.mimeType ) &&
            Objects.equals( size, other.size );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( binaryReference, mimeType, size );
    }

    public static Thumbnail from( final BinaryReference binaryReference, final String mimeType, final long size )
    {
        return new Thumbnail( binaryReference, mimeType, size );
    }
}
