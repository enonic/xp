package com.enonic.xp.repo.impl.repository;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface RepositoryEntryService
{
    RepositoryEntry createRepositoryEntry( RepositoryEntry repository );

    RepositoryEntry updateRepositoryEntry( RepositoryEntry repository, BinaryAttachments binaryAttachments );

    RepositoryIds findRepositoryEntryIds();

    RepositoryEntry getRepositoryEntry( RepositoryId repositoryId );

    void deleteRepositoryEntry( RepositoryId repositoryId );

    ByteSource getBinary( AttachedBinary attachedBinary );
}
