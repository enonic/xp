package com.enonic.xp.repo.impl.dump.reader;

import com.google.common.io.LineProcessor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

public interface DumpReader
{
    void load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor );

    NodeVersion get( final NodeVersionId nodeVersionId );

}
