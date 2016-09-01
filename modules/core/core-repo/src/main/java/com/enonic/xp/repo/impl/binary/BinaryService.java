package com.enonic.xp.repo.impl.binary;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;

public interface BinaryService
{
    AttachedBinary store( final BinaryAttachment binaryAttachment );

    ByteSource get( final AttachedBinary key );

}
