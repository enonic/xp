package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public class AttachedBinary
{
    private final BinaryReference binaryReference;

    private final String blobKey;

    public AttachedBinary( final BinaryReference binaryReference, final String key )
    {
        this.binaryReference = binaryReference;
        this.blobKey = key;
    }

    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }

    public String getBlobKey()
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
        return blobKey.equals( that.blobKey );
    }

    @Override
    public int hashCode()
    {
        int result = binaryReference.hashCode();
        result = 31 * result + blobKey.hashCode();
        return result;
    }
}
