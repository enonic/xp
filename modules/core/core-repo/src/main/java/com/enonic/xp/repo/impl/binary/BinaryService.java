package com.enonic.xp.repo.impl.binary;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.repository.RepositoryId;

public interface BinaryService
{
    AttachedBinary store( final RepositoryId repositoryId, final BinaryAttachment binaryAttachment );

    ByteSource get( final RepositoryId repositoryId, final AttachedBinary key );

}
