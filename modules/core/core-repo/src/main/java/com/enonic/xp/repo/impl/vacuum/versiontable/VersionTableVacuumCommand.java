package com.enonic.xp.repo.impl.vacuum.versiontable;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.vacuum.blob.IsBlobUsedByVersionCommand;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class VersionTableVacuumCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( VersionTableVacuumCommand.class );

    private final NodeService nodeService;

    private final BranchService branchService;

    private final RepositoryService repositoryService;

    private final VersionService versionService;

    private final BlobStore blobStore;

    private final Instant until;

    private final Instant untilForTransientRepository;

    private final VacuumListener listener;

    private final int batchSize;

    private VacuumTaskResult.Builder result;

    private enum BRANCH_CHECK_RESULT
    {
        SAME_VERSION_FOUND, OTHER_VERSION_FOUND, NO_VERSION_FOUND
    }

    private VersionTableVacuumCommand( final Builder builder )
    {
        nodeService = builder.nodeService;
        repositoryService = builder.repositoryService;
        versionService = builder.versionService;
        blobStore = builder.blobStore;
        branchService = builder.branchService;
        listener = builder.params.getListener();
        batchSize = builder.params.getVersionsBatchSize();

        final Instant now = builder.clock != null ? Instant.now( builder.clock ) : Instant.now();
        until = now.minusMillis( builder.params.getAgeThreshold() );
        untilForTransientRepository = now.minus( 1, ChronoUnit.MINUTES );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumTaskResult.Builder execute()
    {
        this.result = VacuumTaskResult.create();

        this.repositoryService.list().forEach( this::processRepository );
        return result;
    }

    private void processRepository( final Repository repository )
    {
        ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( repository.getId() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build()
            .runWith( () -> doProcessRepository( repository ) );
    }

    private void doProcessRepository( final Repository repository )
    {
        int counter = 0;

        NodeVersionId lastVersionId = null;

        final Instant ageThreshold = repository.isTransient() ? untilForTransientRepository : until;

        LOG.debug( "Repo is transient: {}, ageThreshold: {}", repository.isTransient(), ageThreshold );

        NodeVersionQuery query = createQuery( lastVersionId, ageThreshold );
        NodeVersionQueryResult versionsResult = nodeService.findVersions( query );
        long hits = versionsResult.getHits();

        final long totalHits = versionsResult.getTotalHits();

        if ( listener != null )
        {
            listener.stepBegin( repository.getId().toString(), totalHits );
        }

        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        while ( hits > 0 )
        {
            final Set<BlobKey> nodeBlobToCheckSet = new HashSet<>();
            final Set<BlobKey> binaryBlobToCheckSet = new HashSet<>();

            final NodeVersionsMetadata versions = versionsResult.getNodeVersionsMetadata();

            for ( NodeVersionMetadata version : versions )
            {
                final boolean toDelete = processVersion( repository, version );
                if ( toDelete )
                {
                    if ( checkThatVersionIsOld( version, ageThreshold ) )
                    {
                        LOG.debug( "Version's timestamp = '{}', nodeId = '{}', versionId = '{}'", version.getTimestamp(),
                                   version.getNodeId(), version.getNodeVersionId() );
                        result.deleted();
                        versionService.delete( version.getNodeVersionId(), context );
                        nodeBlobToCheckSet.add( version.getNodeVersionKey().getNodeBlobKey() );
                        binaryBlobToCheckSet.addAll( version.getBinaryBlobKeys().getSet() );
                    }
                    else
                    {
                        result.inUse();
                    }
                }
                else
                {
                    result.inUse();
                }
                lastVersionId = version.getNodeVersionId();
                counter++;
            }

            nodeBlobToCheckSet.stream()
                .filter( blobKey -> !isBlobKeyUsed( blobKey, VersionIndexPath.NODE_BLOB_KEY ) )
                .forEach( blobKey -> removeNodeBlobRecord( repository.getId(), NodeConstants.NODE_SEGMENT_LEVEL, blobKey ) );

            binaryBlobToCheckSet.stream()
                .filter( blobKey -> !isBlobKeyUsed( blobKey, VersionIndexPath.BINARY_BLOB_KEYS ) )
                .forEach( blobKey -> removeNodeBlobRecord( repository.getId(), NodeConstants.BINARY_SEGMENT_LEVEL, blobKey ) );

            query = createQuery( lastVersionId, ageThreshold );
            versionsResult = nodeService.findVersions( query );
            hits = versionsResult.getHits();
        }

        if ( listener != null )
        {
            listener.processed( counter );
        }
    }

    private boolean isBlobKeyUsed( final BlobKey blobKey, final IndexPath fieldPath )
    {
        return IsBlobUsedByVersionCommand.create().nodeService( nodeService ).fieldPath( fieldPath ).blobKey( blobKey ).build().execute();
    }

    private void removeNodeBlobRecord( final RepositoryId repositoryId, final SegmentLevel blobType, final BlobKey blobKey )
    {
        LOG.debug( "No other version found using {} blob [{}]", blobType, blobKey );
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, blobType );
        blobStore.removeRecord( segment, blobKey );
    }

    private boolean processVersion( final Repository repository, final NodeVersionMetadata version )
    {
        result.processed();

        switch ( findVersionsInBranches( repository, version ) )
        {
            case NO_VERSION_FOUND:
                LOG.debug( "No version found in branch for [{}/ {}]", version.getNodeId(), version.getNodeVersionId() );
                return true;
            case OTHER_VERSION_FOUND:
                LOG.debug( "Other version found in branch for [{}/ {}]", version.getNodeId(), version.getNodeVersionId() );
                return version.getNodeCommitId() == null;
            default:
                return false;
        }
    }

    private BRANCH_CHECK_RESULT findVersionsInBranches( final Repository repository, final NodeVersionMetadata versionMetadata )
    {
        final NodeId nodeId = versionMetadata.getNodeId();
        final NodeVersionId versionId = versionMetadata.getNodeVersionId();

        boolean nodeFound = false;
        for ( final Branch branch : repository.getBranches() )
        {
            final InternalContext context =
                InternalContext.create( ContextAccessor.current() ).branch( branch ).repositoryId( repository.getId() ).build();

            final NodeBranchEntry node = this.branchService.get( nodeId, context );
            if ( node != null )
            {
                if ( versionId.equals( node.getVersionId() ) )
                {
                    return BRANCH_CHECK_RESULT.SAME_VERSION_FOUND;
                }
                else
                {
                    nodeFound = true;
                }
            }
        }
        return nodeFound ? BRANCH_CHECK_RESULT.OTHER_VERSION_FOUND : BRANCH_CHECK_RESULT.NO_VERSION_FOUND;
    }

    private NodeVersionQuery createQuery( NodeVersionId lastVersionId, Instant ageThreshold )
    {
        final NodeVersionQuery.Builder builder = NodeVersionQuery.create();

        if ( lastVersionId != null )
        {
            final RangeFilter versionIdFilter = RangeFilter.create()
                .fieldName( VersionIndexPath.VERSION_ID.getPath() )
                .gt( ValueFactory.newString( lastVersionId.toString() ) )
                .build();
            builder.addQueryFilter( versionIdFilter );
        }

        final RangeFilter mustBeOlderThanFilter =
            RangeFilter.create().fieldName( VersionIndexPath.TIMESTAMP.getPath() ).to( ValueFactory.newDateTime( ageThreshold ) ).build();

        return builder.addQueryFilter( mustBeOlderThanFilter )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.VERSION_ID, OrderExpr.Direction.ASC ) )
            .size( batchSize )
            .build();
    }

    private boolean checkThatVersionIsOld( NodeVersionMetadata previousVersion, Instant ageThreshold )
    {
        final NodeVersionQuery query = findNextVersion( previousVersion.getNodeVersionId(), ageThreshold );
        final NodeVersionQueryResult versionsResult = nodeService.findVersions( query );
        final long hits = versionsResult.getHits();

        if ( hits == 0 )
        {
            LOG.debug( "No next version found for version [{}].", previousVersion.getNodeVersionId() );
            return true;
        }

        final NodeVersionMetadata nextVersion = versionsResult.getNodeVersionsMetadata().iterator().next();

        final Instant tsOfPrevVersion = previousVersion.getTimestamp();
        final Instant tsOfNextVersion = nextVersion.getTimestamp();

        final boolean shouldBeDeleted = tsOfPrevVersion.isBefore( ageThreshold ) && ageThreshold.isBefore( tsOfNextVersion );

        LOG.debug(
            "Checking if version [{}] should be deleted. tsOfPrevVersion: {}, tsOfNextVersion: {}, ageThreshold: {}. Should be deleted: {}",
            previousVersion.getNodeVersionId(), tsOfPrevVersion, tsOfNextVersion, ageThreshold, shouldBeDeleted );

        return shouldBeDeleted;
    }

    private NodeVersionQuery findNextVersion( NodeVersionId lastVersionId, Instant ageThreshold )
    {
        final NodeVersionQuery.Builder builder = NodeVersionQuery.create();

        if ( lastVersionId != null )
        {
            final RangeFilter versionIdFilter = RangeFilter.create()
                .fieldName( VersionIndexPath.VERSION_ID.getPath() )
                .gt( ValueFactory.newString( lastVersionId.toString() ) )
                .build();
            builder.addQueryFilter( versionIdFilter );
        }

        final RangeFilter filter =
            RangeFilter.create().fieldName( VersionIndexPath.TIMESTAMP.getPath() ).from( ValueFactory.newDateTime( ageThreshold ) ).build();

        return builder.addQueryFilter( filter )
            .addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.ASC ) )
            .size( 1 )
            .build();
    }

    public static final class Builder
    {
        private Clock clock;

        private NodeService nodeService;

        private RepositoryService repositoryService;

        private VersionService versionService;

        private BlobStore blobStore;

        private BranchService branchService;

        private VacuumTaskParams params;

        private Builder()
        {
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public Builder blobStore( final BlobStore blobStore )
        {
            this.blobStore = blobStore;
            return this;
        }

        public Builder params( final VacuumTaskParams params )
        {
            this.params = params;
            return this;
        }

        public Builder branchService( final BranchService branchService )
        {
            this.branchService = branchService;
            return this;
        }

        public Builder clock( final Clock clock )
        {
            this.clock = clock;
            return this;
        }

        public VersionTableVacuumCommand build()
        {
            return new VersionTableVacuumCommand( this );
        }
    }
}
