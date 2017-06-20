package com.enonic.xp.impl.server.rest;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class SystemDumpListenerImpl
    implements SystemDumpListener
{
    private final ProgressReporter progressReporter;

    private int progressCount = 0;

    private Long total;

    public SystemDumpListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void dumpingBranch( final RepositoryId repositoryId, final Branch branch )
    {
        this.progressReporter.info( String.format( "Dumping repo [%s] - branch [%s]", repositoryId, branch ) );
    }

    @Override
    public void nodeDumped()
    {
        this.progressReporter.progress( ++this.progressCount, total != null ? total.intValue() : -11 );
    }

    @Override
    public void setTotal( final Long total )
    {
        this.total = total;
    }
}
