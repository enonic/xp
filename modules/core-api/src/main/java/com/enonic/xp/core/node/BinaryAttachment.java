package com.enonic.xp.core.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.util.BinaryReference;

public class BinaryAttachment
{
    private final BinaryReference reference;

    private final ByteSource byteSource;

    public BinaryAttachment( final BinaryReference reference, final ByteSource byteSource )
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


