package com.enonic.xp.repo.impl.dump;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repo.impl.dump.reader.EntryLoadResult;
import com.enonic.xp.repository.RepositoryId;

public class TestDumpReader
    implements DumpReader
{
    @Override
    public Branches getBranches( final RepositoryId repositoryId )
    {
        return null;
    }

    @Override
    public void load( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor )
    {



    }

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        return null;
    }


    @Override
    public ByteSource getBinary( final String blobKey )
    {
        return null;
    }
}



