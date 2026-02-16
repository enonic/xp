package com.enonic.xp.repo.impl.vacuum.segment;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class SegmentVacuumTask
    implements VacuumTask
{
    private static final int ORDER = 0;

    private static final String NAME = "SegmentVacuumTask";

    private BlobStore blobStore;

    private RepositoryService repositoryService;

    private NodeService nodeService;

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
        return true;
    }

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        if (params.hasListener()) {
            params.getListener().taskBegin( NAME, null );
        }
        return SegmentVacuumCommand.create().
            blobStore( blobStore ).
            repositoryService( repositoryService ).
            nodeService( nodeService ).
            params( params ).
            build().
            execute().
            taskName( NAME ).
            build();
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
