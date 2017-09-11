package com.enonic.xp.repo.impl.vacuum.versiontable;


import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.vacuum.AbstractVacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class VersionTableCleanupTask
    extends AbstractVacuumTask
{
    private final NodeService nodeService;

    public VersionTableCleanupTask( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public int order()
    {
        return 300;
    }

    @Override
    public String name()
    {
        return "UnusedVersionTableEntryCleaner";
    }

    public VacuumTaskResult execute( final VacuumTaskParams params )
    {

        final NodeVersionQueryResult allVersions = this.nodeService.findVersions( NodeVersionQuery.create().
                                                                                      // addQueryFilter(  ); //timestamp-filter
                                                                                          build() );

        System.out.println( "Found: " + allVersions.getTotalHits() + " versions to consider" );

        return null;
    }
}
