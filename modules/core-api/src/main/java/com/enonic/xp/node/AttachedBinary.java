package com.enonic.xp.node;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.util.BinaryReference;

public class AttachedBinary
{
    private final BinaryReference binaryReference;

    private final BlobKey blobKey;

    public AttachedBinary( final BinaryReference binaryReference, final BlobKey blobKey )
    {
        this.binaryReference = binaryReference;
        this.blobKey = blobKey;
    }

    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
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

        final AttachedBinary that = (AttachedBinary) o;

        if ( !binaryReference.equals( that.binaryReference ) )
        {
            return false;
        }
        if ( !blobKey.equals( that.blobKey ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = binaryReference.hashCode();
        result = 31 * result + blobKey.hashCode();
        return result;
    }
}
