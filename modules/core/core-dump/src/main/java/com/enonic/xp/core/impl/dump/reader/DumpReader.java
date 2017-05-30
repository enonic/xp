package com.enonic.xp.core.impl.dump.reader;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.repository.RepositoryId;

public interface DumpReader
{
    void open( final RepositoryId repositoryId, final Branch branch );

    void close();

    DumpEntry next();

}
