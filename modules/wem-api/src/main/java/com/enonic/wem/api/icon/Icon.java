package com.enonic.wem.api.icon;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;

public final class Icon
{
    private final BlobKey blobKey;

    private final String mimeType;

    private Icon( final BlobKey blobKey, final String mimeType )
    {
        Preconditions.checkNotNull( mimeType, "mimeType is mandatory for an icon" );
        Preconditions.checkNotNull( blobKey, "blobKey is mandatory" );
        this.blobKey = blobKey;
        this.mimeType = mimeType;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Icon ) )
        {
            return false;
        }

        final Icon that = (Icon) o;
        return Objects.equal( this.mimeType, that.mimeType ) && Objects.equal( this.blobKey, that.blobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( mimeType, blobKey );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "mimeType", mimeType ).
            add( "blobKey", blobKey ).
            toString();
    }

    public static Icon from( final BlobKey blobKey, final String mimeType )
    {
        return new Icon( blobKey, mimeType );
    }
}
