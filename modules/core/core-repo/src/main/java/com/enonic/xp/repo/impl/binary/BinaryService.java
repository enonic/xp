package com.enonic.xp.repo.impl.binary;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryAttachment;

public interface BinaryService
{
    AttachedBinary store( final RepositoryId repositoryId, final BinaryAttachment binaryAttachment );

    ByteSource get( final RepositoryId repositoryId, final com.enonic.xp.util.AttachedBinary key );

}
