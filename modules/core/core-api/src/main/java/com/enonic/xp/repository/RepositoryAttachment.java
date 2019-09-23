package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.util.BinaryReference;

@Beta
public final class RepositoryAttachment
{
    private final BinaryReference binaryReference;

    private final String blobKey;

    public RepositoryAttachment( final BinaryReference binaryReference, final String blobKey )
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
        final RepositoryAttachment that = (RepositoryAttachment) o;
        return Objects.equals( binaryReference, that.binaryReference ) && Objects.equals( blobKey, that.blobKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( binaryReference, blobKey );
    }
}
