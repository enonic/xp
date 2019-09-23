package com.enonic.xp.repository;

import com.google.common.io.ByteSource;

import com.enonic.xp.util.BinaryReference;

public final class RepositoryBinaryAttachment
{
    private final BinaryReference reference;

    private final ByteSource byteSource;

    public RepositoryBinaryAttachment( final BinaryReference reference, final ByteSource byteSource )
    {
        this.reference = reference;
        this.byteSource = byteSource;
    }

    public BinaryReference getReference()
    {
        return reference;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }
}
