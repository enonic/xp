package com.enonic.xp.util;

import java.util.Objects;

import com.google.common.annotations.Beta;

/**
 * AttachedBinary will be final in future versions and must not be subclassed.
 * The only exception is {@link com.enonic.xp.node.AttachedBinary}, until it gets deprecated removed
 */
@Beta
public class AttachedBinary
{
    private final BinaryReference binaryReference;

    private final String blobKey;

    public AttachedBinary( final BinaryReference binaryReference, final String blobKey )
    {
        this.binaryReference = binaryReference;
        this.blobKey = blobKey;
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
        return Objects.equals( binaryReference, that.binaryReference ) && Objects.equals( blobKey, that.blobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( binaryReference, blobKey );
    }
}
