package com.enonic.xp.repo.impl.vacuum.version;


import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.AbstractVacuumTask;
import com.enonic.xp.repo.impl.vacuum.EntryState;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class VersionFileCleanerTask
    extends AbstractVacuumTask
    implements VacuumTask
{
    private final static Logger LOG = LoggerFactory.getLogger( VersionFileCleanerTask.class );

    private BlobStore blobStore;

    private NodeInUseDetector nodeInUseDetector;

    private NodeIdResolver nodeIdResolver;

    @Override
    public int order()
    {
        return 100;
    }

    @Override
    public String name()
    {
        return "UnusedVersionFilesCleaner";
    }

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        final VacuumTaskResult.Builder result = VacuumTaskResult.create().taskName( this.name() );

        doExecute( result, params.getAgeThreshold(), params.getListener() );

        return result.build();
    }

    private void doExecute( final VacuumTaskResult.Builder result, final long ageThreshold, final VacuumListener listener )
    {
        List<BlobKey> toBeRemoved = Lists.newArrayList();

        LOG.info( "Traversing node-folder....." );

        if ( listener != null )
        {
            listener.vacuumingBlobSegment( NodeConstants.NODE_SEGMENT );
        }

        this.blobStore.list( NodeConstants.NODE_SEGMENT ).
            forEach( rec -> {
                if ( includeRecord( rec, ageThreshold ) )
                {
                    handleEntry( rec, result, toBeRemoved );
                }

                if ( listener != null )
                {
                    listener.vacuumingBlob( 1L );
                }
            } );

        toBeRemoved.forEach( key -> this.blobStore.removeRecord( NodeConstants.NODE_SEGMENT, key ) );
    }

    private void handleEntry( final BlobRecord record, final VacuumTaskResult.Builder result, final List<BlobKey> toBeRemoved )
    {
        final EntryState entryState = resolveState( record );
        report( entryState, result );
        if ( entryState == EntryState.NOT_IN_USE )
        {
            toBeRemoved.add( record.getKey() );
        }
    }

    private EntryState resolveState( final BlobRecord record )
    {
        final NodeId nodeId = this.nodeIdResolver.resolve( record );

        if ( nodeId != null )
        {
            final boolean inUse;
            try
            {
                inUse = nodeInUseDetector.execute( nodeId );
            }
            catch ( Exception e )
            {
                return EntryState.FAILED;
            }

            if ( inUse )
            {
                return EntryState.IN_USE;
            }
            else
            {
                return EntryState.NOT_IN_USE;
            }
        }

        return EntryState.NOT_FOUND;
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Reference
    public void setNodeInUseDetector( final NodeInUseDetector nodeInUseDetector )
    {
        this.nodeInUseDetector = nodeInUseDetector;
    }

    @Reference
    public void setNodeIdResolver( final NodeIdResolver nodeIdResolver )
    {
        this.nodeIdResolver = nodeIdResolver;
    }
}
