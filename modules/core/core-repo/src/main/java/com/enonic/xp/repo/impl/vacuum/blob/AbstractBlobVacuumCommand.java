package com.enonic.xp.repo.impl.vacuum.blob;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.vacuum.VacuumTaskResult;

public abstract class AbstractBlobVacuumCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractBlobVacuumCommand.class );

    private final BlobStore blobStore;

    private final NodeService nodeService;

    private final VacuumTaskParams params;

    private VacuumTaskResult.Builder result;

    protected AbstractBlobVacuumCommand( final Builder builder )
    {
        blobStore = builder.blobStore;
        nodeService = builder.nodeService;
        params = builder.params;
    }

    public VacuumTaskResult.Builder execute()
    {
        this.result = VacuumTaskResult.create();

        this.blobStore.listSegments().
            filter( segment -> RepositorySegmentUtils.hasBlobTypeLevel( segment, getBlobTypeSegmentLevel() ) ).
            forEach( this::processBinarySegment );
        return result;
    }

    protected abstract SegmentLevel getBlobTypeSegmentLevel();

    protected abstract IndexPath getFieldIndexPath();


    private void processBinarySegment( final Segment segment )
    {
        if ( params.hasListener() )
        {
            params.getListener().stepBegin( segment.toString(), null );
        }

        final List<BlobKey> blobToDelete;
        try (Stream<BlobRecord> list = blobStore.list( segment ))
        {
            blobToDelete = list.filter( blobRecord -> shouldDelete( segment, blobRecord ) )
                .map( BlobRecord::getKey ).collect( Collectors.toList() );
        }
        blobToDelete.forEach( blobKey -> blobStore.removeRecord( segment, blobKey ) );
    }

    private boolean shouldDelete( final Segment segment, final BlobRecord blobRecord )
    {
        if ( isOldBlobRecord( blobRecord ) )
        {
            result.processed();
            if ( params.hasListener() )
            {
                params.getListener().processed( 1L );
            }

            final BlobKey blobKey = blobRecord.getKey();
            if ( !isUsedByVersion( segment, blobKey ) )
            {
                LOG.debug( "No version found for {} [{}]", getFieldIndexPath(), blobKey );
                result.deleted();
                return true;
            } else {
                result.inUse();
            }
        }

        return false;
    }

    private boolean isOldBlobRecord( final BlobRecord blobRecord )
    {
        return params.getVacuumStartedAt().toEpochMilli() - blobRecord.lastModified() >= params.getAgeThreshold();
    }

    private boolean isUsedByVersion( final Segment segment, final BlobKey blobKey )
    {
        final RepositoryId repositoryId = RepositorySegmentUtils.toRepositoryId( segment );
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repositoryId ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().
            callWith( () -> isUsedByVersion( blobKey ) );
    }

    private boolean isUsedByVersion( final BlobKey blobKey )
    {
        return IsBlobUsedByVersionCommand.create().
            nodeService( nodeService ).
            fieldPath( getFieldIndexPath() ).
            blobKey( blobKey ).
            build().
            execute();
    }

    public static class Builder<B extends Builder>
    {
        private BlobStore blobStore;

        private NodeService nodeService;

        private VacuumTaskParams params;

        protected Builder()
        {
        }

        public B blobStore( final BlobStore blobStore )
        {
            this.blobStore = blobStore;
            return (B) this;
        }

        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        public B params( final VacuumTaskParams params )
        {
            this.params = params;
            return (B) this;
        }
    }
}
