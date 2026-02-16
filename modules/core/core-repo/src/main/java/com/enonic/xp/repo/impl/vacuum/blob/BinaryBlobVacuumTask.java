package com.enonic.xp.repo.impl.vacuum.blob;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class BinaryBlobVacuumTask
    implements VacuumTask
{
    private static final int ORDER = 200;

    private static final String NAME = "BinaryBlobVacuumTask";

    private final NodeService nodeService;

    private final BlobStore blobStore;

    @Activate
    public BinaryBlobVacuumTask( @Reference final NodeService nodeService, @Reference final BlobStore blobStore )
    {
        this.nodeService = nodeService;
        this.blobStore = blobStore;
    }

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        if ( params.hasListener() )
        {
            params.getListener().taskBegin( NAME, null );
        }
        return BinaryBlobVacuumCommand.create()
            .blobStore( blobStore )
            .nodeService( nodeService )
            .params( params )
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
        return true;
    }


}
