package com.enonic.xp.util;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

/**
 * BinaryAttachment is will be final in future versions and must not be subclassed.
 * The only exception is {@link com.enonic.xp.node.BinaryAttachment}, until it gets deprecated removed
 */
@Beta
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
