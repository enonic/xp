package com.enonic.xp.repo.impl.vacuum.binary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.node.NodeSegmentUtils;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.vacuum.versiontable.VersionTableVacuumCommand;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class BinaryVacuumCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( VersionTableVacuumCommand.class );

    private final BlobStore blobStore;

    private final NodeService nodeService;

    private final VacuumTaskParams params;

    private VacuumTaskResult.Builder result;

    private BinaryVacuumCommand( final Builder builder )
    {
        blobStore = builder.blobStore;
        nodeService = builder.nodeService;
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public VacuumTaskResult.Builder execute()
    {
        this.result = VacuumTaskResult.create();

        this.blobStore.listSegments().
            filter( NodeSegmentUtils::isBinarySegment ).
            forEach( this::processBinarySegment );
        return result;
    }

    private NodeVersionQuery createQuery( final BlobKey blobKey )
    {
        final ValueFilter mustHaveBinaryBlobKey = ValueFilter.create().
            fieldName( VersionIndexPath.BINARY_BLOB_KEYS.getPath() ).
            addValue( ValueFactory.newString( blobKey.toString() ) ).
            build();

        return NodeVersionQuery.create().
            size( 0 ).
            addQueryFilter( mustHaveBinaryBlobKey ).
            build();
    }

    private void processBinarySegment( final Segment segment )
    {
        if ( params.getListener() != null )
        {
            params.getListener().vacuumingBlobSegment( segment );
        }

        final List<BlobKey> blobToDelete = new ArrayList<>();
        blobStore.list( segment ).
            filter( blobRecord -> shouldDelete( segment, blobRecord ) ).
            forEach( blobRecord -> blobToDelete.add( blobRecord.getKey() ) );

        blobToDelete.forEach( blobKey -> blobStore.removeRecord( segment, blobKey ) );
    }

    private boolean shouldDelete( final Segment segment, final BlobRecord blobRecord )
    {
        if ( isOldBlobRecord( blobRecord ) )
        {
            result.processed();
            if ( params.getListener() != null )
            {
                params.getListener().vacuumingBlob( 1L );
            }

            final BlobKey blobKey = blobRecord.getKey();
            if ( !isUsedByVersion( segment, blobKey ) )
            {
                LOG.debug( "No version found in branch for binaryBlobKey [" + blobKey + "]" );
                result.deleted();
                return true;
            }
            result.inUse();
        }

        return false;
    }

    private boolean isOldBlobRecord( final BlobRecord blobRecord )
    {
        return System.currentTimeMillis() - blobRecord.lastModified() >= params.getAgeThreshold();
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
        final NodeVersionQuery query = createQuery( blobKey );
        final NodeVersionQueryResult versions = nodeService.findVersions( query );
        return versions.getTotalHits() > 0;
    }

    public static final class Builder
    {
        private BlobStore blobStore;

        private NodeService nodeService;

        private VacuumTaskParams params;

        private Builder()
        {
        }

        public Builder blobStore( final BlobStore blobStore )
        {
            this.blobStore = blobStore;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder params( final VacuumTaskParams params )
        {
            this.params = params;
            return this;
        }

        public BinaryVacuumCommand build()
        {
            return new BinaryVacuumCommand( this );
        }
    }
}
