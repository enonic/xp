package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.util.BinaryReference;

@Beta
public class AttachedBinary
    extends com.enonic.xp.util.AttachedBinary
{
    public AttachedBinary( final BinaryReference binaryReference, final String key )
    {
        super( binaryReference, key );
    }

    public BinaryReference getBinaryReference()
    {
        return super.getBinaryReference();
    }

    public String getBlobKey()
    {
        return super.getBlobKey();
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
