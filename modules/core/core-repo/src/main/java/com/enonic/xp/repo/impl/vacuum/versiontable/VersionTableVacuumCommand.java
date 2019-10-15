package com.enonic.xp.repo.impl.vacuum.versiontable;

import java.time.Instant;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.executor.BatchedGetVersionsExecutor;
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

    private NodeService nodeService;

    private RepositoryService repositoryService;

    private VersionService versionService;

    private BlobStore blobStore;

    private final VacuumTaskParams params;

    private NodeVersionQuery query;

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
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumTaskResult.Builder execute()
    {
        this.query = createQuery();
        this.result = VacuumTaskResult.create();

        this.repositoryService.list().
            forEach( this::processRepository );
        return result;
    }

    private void processRepository( final Repository repository )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().
            runWith( () -> doProcessRepository( repository ) );
    }

    private void doProcessRepository( final Repository repository )
    {
        final BatchedGetVersionsExecutor executor = BatchedGetVersionsExecutor.create().
            query( query ).
            nodeService( nodeService ).
            build();

        final VacuumListener listener = params.getListener();
        if ( listener != null )
        {
            final Long versionTotal = executor.getTotalHits();
            listener.stepBegin( repository.getId().toString(), versionTotal );
        }


        final HashSet<NodeVersionId> versionToDeleteSet = Sets.newHashSet();
        final HashSet<BlobKey> nodeBlobToCheckSet = Sets.newHashSet();
        final HashSet<BlobKey> binaryBlobToCheckSet = Sets.newHashSet();
        while ( executor.hasMore() )
        {
            final NodeVersionsMetadata versions = executor.execute();

            versions.forEach( ( version ) -> {
                final boolean toDelete = processVersion( repository, version );
                if ( toDelete )
                {
                    result.deleted();
                    versionToDeleteSet.add( version.getNodeVersionId() );
                    nodeBlobToCheckSet.add( version.getNodeVersionKey().getNodeBlobKey() );
                    version.getBinaryBlobKeys().forEach( binaryBlobToCheckSet::add );
                }
                else
                {
                    result.inUse();
                }
            } );

            if ( listener != null )
            {
                listener.processed( versions.size() );
            }
        }

        versionService.delete( versionToDeleteSet, InternalContext.from( ContextAccessor.current() ) );

        nodeBlobToCheckSet.stream().
            filter( blobKey -> !isBlobKeyUsed( blobKey, VersionIndexPath.NODE_BLOB_KEY ) ).
            forEach( blobKey -> removeNodeBlobRecord( repository.getId(), NodeConstants.NODE_SEGMENT_LEVEL, blobKey ) );

        binaryBlobToCheckSet.stream().
            filter( blobKey -> !isBlobKeyUsed( blobKey, VersionIndexPath.BINARY_BLOB_KEYS ) ).
            forEach( blobKey -> removeNodeBlobRecord( repository.getId(), NodeConstants.BINARY_SEGMENT_LEVEL, blobKey ) );
    }

    private boolean isBlobKeyUsed( final BlobKey blobKey, final IndexPath fieldPath )
    {
        return !IsBlobUsedByVersionCommand.create().
            nodeService( nodeService ).
            fieldPath( fieldPath ).
            blobKey( blobKey ).
            build().
            execute();
    }

    private void removeNodeBlobRecord( final RepositoryId repositoryId, final SegmentLevel blobType, final BlobKey blobKey )
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "No other version found using " + blobType.toString() + " blob [" + blobKey + "]" );
        }
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, blobType );
        blobStore.removeRecord( segment, blobKey );
    }

    private boolean processVersion( final Repository repository, final NodeVersionMetadata version )
    {
        result.processed();

        switch ( findVersionsInBranches( repository, version ) )
        {
            case NO_VERSION_FOUND:
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "No version found in branch for for [" + version.getNodeId() + "/ " + version.getNodeVersionId() + "]" );
                }
                return true;
            case OTHER_VERSION_FOUND:
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Other version found in branch for for [" + version.getNodeId() + "/ " + version.getNodeVersionId() + "]" );
                }
                return version.getNodeCommitId() == null;
        }
        return false;
    }

    private BRANCH_CHECK_RESULT findVersionsInBranches( final Repository repository, final NodeVersionMetadata versionMetadata )
    {
        final NodeId nodeId = versionMetadata.getNodeId();
        final NodeVersionId versionId = versionMetadata.getNodeVersionId();

        boolean nodeFound = false;
        for ( final Branch branch : repository.getBranches() )
        {
            try
            {
                final Node node = ContextBuilder.from( ContextAccessor.current() ).
                    branch( branch ).
                    repositoryId( repository.getId() ).
                    build().callWith( () -> this.nodeService.getById( nodeId ) );

                if ( versionId.equals( node.getNodeVersionId() ) )
                {
                    return BRANCH_CHECK_RESULT.SAME_VERSION_FOUND;
                }

                nodeFound = true;
            }
            catch ( NodeNotFoundException e )
            {
                // Ignore
            }
        }
        return nodeFound ? BRANCH_CHECK_RESULT.OTHER_VERSION_FOUND : BRANCH_CHECK_RESULT.NO_VERSION_FOUND;
    }

    private NodeVersionQuery createQuery()
    {
        final Instant until = Instant.now().minusMillis( params.getAgeThreshold() );
        final RangeFilter mustBeOlderThanFilter = RangeFilter.create().
            fieldName( VersionIndexPath.TIMESTAMP.getPath() ).
            to( ValueFactory.newDateTime( until ) ).
            build();

        return NodeVersionQuery.create().
            addQueryFilter( mustBeOlderThanFilter ).
            build();
    }

    public static final class Builder
    {
        private NodeService nodeService;

        private RepositoryService repositoryService;

        private VersionService versionService;

        private BlobStore blobStore;

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

        public VersionTableVacuumCommand build()
        {
            return new VersionTableVacuumCommand( this );
        }
    }
}
