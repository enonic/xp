package com.enonic.xp.node;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.util.BinaryReference;


@Beta
public class BinaryAttachment
    extends com.enonic.xp.util.BinaryAttachment
{
    public BinaryAttachment( final BinaryReference reference, final ByteSource byteSource )
    {
        super( reference, byteSource );
    }

    public BinaryReference getReference()
    {
        return super.getReference();
    }

    public ByteSource getByteSource()
    {
        return super.getByteSource();
    }
}
