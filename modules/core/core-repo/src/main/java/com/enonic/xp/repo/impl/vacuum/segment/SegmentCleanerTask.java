package com.enonic.xp.repo.impl.vacuum.segment;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class SegmentCleanerTask
    implements VacuumTask
{
    private static final int ORDER = 0;

    private static final String NAME = "UnusedSegmentsCleaner";

    private BlobStore blobStore;

    private RepositoryService repositoryService;

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
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        return SegmentCleanerCommand.create().
            name( NAME ).
            blobStore( blobStore ).
            repositoryService( repositoryService ).
            build().
            execute( params );
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
}
