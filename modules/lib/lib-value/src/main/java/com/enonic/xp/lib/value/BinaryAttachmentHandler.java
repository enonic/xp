package com.enonic.xp.lib.value;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.util.BinaryReference;

public class BinaryAttachmentHandler
{
    public BinaryAttachment newInstance( final BinaryReference reference, final ByteSource byteSource )
    {
        return new BinaryAttachment( reference, byteSource );
    }
}
