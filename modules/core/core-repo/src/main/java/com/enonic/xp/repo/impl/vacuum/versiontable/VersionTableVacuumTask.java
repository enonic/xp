package com.enonic.xp.repo.impl.vacuum.versiontable;

import java.time.Clock;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class VersionTableVacuumTask
    implements VacuumTask
{
    private static final int ORDER = 100;

    private static final String NAME = "VersionTableVacuumTask";

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final VersionService versionService;

    private final BranchService branchService;

    private final BlobStore blobStore;

    private Clock clock;

    @Activate
    public VersionTableVacuumTask( @Reference final NodeService nodeService, @Reference final RepositoryService repositoryService,
                                   @Reference final VersionService versionService, @Reference final BranchService branchService,
                                   @Reference final BlobStore blobStore )
    {
        this.nodeService = nodeService;
        this.repositoryService = repositoryService;
        this.versionService = versionService;
        this.blobStore = blobStore;
        this.branchService = branchService;
        this.clock = Clock.systemUTC();
    }

    public void setClock( final Clock clock )
    {
        this.clock = clock;
    }

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        if ( params.hasListener() )
        {
            params.getListener().taskBegin( NAME, null );
        }
        return VersionTableVacuumCommand.create()
            .repositoryService( repositoryService )
            .nodeService( nodeService )
            .versionService( versionService )
            .branchService( branchService )
            .blobStore( blobStore )
            .params( params )
            .clock( clock )
            .build()
            .execute()
            .taskName( NAME )
            .build();
    }

    @Override
    public int order()
    {
        return ORDER;
    }

    @Override
    public String name()
    {
        return NAME;
    }

    @Override
    public boolean deletesBlobs()
    {
        return false;
    }
}
