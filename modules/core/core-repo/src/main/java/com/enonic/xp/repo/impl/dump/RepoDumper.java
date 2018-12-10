package com.enonic.xp.repo.impl.dump;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.node.executor.BatchedGetChildrenExecutor;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

class RepoDumper
{
    private static final int DEFAULT_BATCH_SIZE = 5000;

    private final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final DumpWriter writer;

    private final RepoDumpResult.Builder dumpResult;

    private final SystemDumpListener listener;

    private RepoDumper( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.includeVersions = builder.includeVersions;
        this.includeBinaries = builder.includeBinaries;
        this.nodeService = builder.nodeService;
        this.repositoryService = builder.repositoryService;
        this.writer = builder.writer;
        this.dumpResult = RepoDumpResult.create( this.repositoryId );
        this.maxAge = builder.maxAge;
        this.maxVersions = builder.maxVersions;
        this.listener = builder.listener;
    }

    public RepoDumpResult execute()
    {
        final Set<NodeId> dumpedNodes = Sets.newHashSet();

        getBranches().forEach( ( branch ) -> dumpedNodes.addAll( setContext( branch ).callWith( this::doExecute ) ) );

        if ( this.includeVersions )
        {
            setContext( RepositoryConstants.MASTER_BRANCH ).runWith( () -> dumpVersions( dumpedNodes ) );
        }

        return this.dumpResult.build();
    }

    private Set<NodeId> doExecute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        Set<NodeId> dumpedNodes = Sets.newHashSet();

        final Branch branch = ContextAccessor.current().getBranch();

        final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( branch );
        try
        {
            writer.openBranchMeta( this.repositoryId, branch );
            dumpedNodes.addAll( dumpBranch( branchDumpResult ) );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Error occurred when dumping repository " + repositoryId, e );
        }
        finally
        {
            writer.close();
        }

        this.dumpResult.add( branchDumpResult.build() );

        return dumpedNodes;
    }

    private Set<NodeId> dumpBranch( final BranchDumpResult.Builder dumpResult )
    {
        Set<NodeId> dumpedNodes = Sets.newHashSet();

        final Node rootNode = this.nodeService.getRoot();
        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( rootNode.id() ).
            recursive( true ).
            batchSize( DEFAULT_BATCH_SIZE ).
            childOrder( ChildOrder.from( "_path asc" ) ).
            build();

        reportDumpingBranch( ContextAccessor.current().getBranch(), executor.getTotalHits() + 1 );

        doDumpNode( rootNode.id(), dumpResult );
        dumpedNodes.add( rootNode.id() );

        while ( executor.hasMore() )
        {
            final NodeIds children = executor.execute();

            for ( final NodeId child : children )
            {
                doDumpNode( child, dumpResult );
                dumpedNodes.add( child );
            }
        }

        return dumpedNodes;
    }

    private void dumpVersions( final Set<NodeId> dumpedNodes )
    {
        try
        {
            writer.openVersionsMeta( this.repositoryId );

            dumpedNodes.stream().forEach( ( nodeId ) -> {

                final VersionsDumpEntry.Builder builder = VersionsDumpEntry.create( nodeId );

                final NodeVersionQueryResult versions = getVersions( nodeId );

                for ( final NodeVersionMetadata metaData : versions.getNodeVersionsMetadata() )
                {
                    doStoreVersion( builder, metaData );
                    this.dumpResult.addedVersion();
                }

                this.writer.writeVersionsEntry( builder.build() );
            } );
        }
        finally
        {
            writer.close();
        }
    }

    private void doStoreVersion( final VersionsDumpEntry.Builder builder, final NodeVersionMetadata metaData )
    {
        final NodeVersion nodeVersion = this.nodeService.getByBlobKey( metaData.getBlobKey() );
        builder.addVersion( VersionMetaFactory.create( nodeVersion, metaData ) );

        storeVersionBlob( metaData );
        storeVersionBinaries( metaData, nodeVersion );
    }

    private void storeVersionBlob( final NodeVersionMetadata metaData )
    {
        try
        {
            this.writer.writeVersionBlob( repositoryId, metaData.getBlobKey() );
        }
        catch ( Exception e )
        {
            // Report
            LOG.error( "Failed to write version for nodeVersion " + metaData.getNodeVersionId(), e );
        }
    }

    private void storeVersionBinaries( final NodeVersionMetadata metaData, final NodeVersion nodeVersion )
    {
        nodeVersion.getAttachedBinaries().forEach( ( attachedBinary ) -> {
            try
            {
                this.writer.writeBinaryBlob( repositoryId, attachedBinary.getBlobKey() );
            }
            catch ( Exception e )
            {
                // Report
                LOG.error(
                    "Failed to write binary for nodeVersion " + metaData.getNodeVersionId() + ", binary " + attachedBinary.getBlobKey(),
                    e );
            }
        } );
    }

    private Branches getBranches()
    {
        final Repository repository = this.repositoryService.get( this.repositoryId );

        if ( repository == null )
        {
            throw new RepoDumpException( String.format( "Repository [%s] not found", this.repositoryId ) );
        }

        return repository.getBranches();
    }

    private Context setContext( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( this.repositoryId ).
            branch( branch ).
            build();
    }


    private void doDumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        try
        {
            final BranchDumpEntry branchDumpEntry = createDumpEntry( nodeId );
            writer.writeBranchEntry( branchDumpEntry );
            writer.writeVersionBlob( repositoryId, branchDumpEntry.getMeta().getBlobKey() );
            writeBinaries( dumpResult, branchDumpEntry );
            dumpResult.addedNode();
            reportNodeDumped();
        }
        catch ( Exception e )
        {
            dumpResult.error( DumpError.error( "Cannot dump node with id [" + nodeId + "]: " + e.getMessage() ) );
        }
    }

    private void writeBinaries( final BranchDumpResult.Builder dumpResult, final BranchDumpEntry branchDumpEntry )
    {
        branchDumpEntry.getBinaryReferences().forEach( ( ref ) -> {
            try
            {
                this.writer.writeBinaryBlob( repositoryId, ref );
            }
            catch ( RepoDumpException e )
            {
                LOG.error( "Cannot dump binary:", e );
                dumpResult.error( DumpError.error( "Cannot dump binary: " + e.getMessage() ) );
            }
        } );
    }

    private void reportNodeDumped()
    {
        if ( this.listener != null )
        {
            this.listener.nodeDumped();
        }
    }

    private BranchDumpEntry createDumpEntry( final NodeId nodeId )
    {
        final BranchDumpEntry.Builder builder = BranchDumpEntry.create().
            nodeId( nodeId );

        final Node currentNode = this.nodeService.getById( nodeId );
        final NodeVersionMetadata currentVersionMetaData = getActiveVersion( nodeId );

        builder.meta( VersionMetaFactory.create( currentNode, currentVersionMetaData ) );

        if ( this.includeBinaries )
        {
            builder.addBinaryReferences(
                currentNode.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );
        }

        return builder.build();
    }

    private NodeVersionMetadata getActiveVersion( final NodeId nodeId )
    {
        final Branch branch = ContextAccessor.current().
            getBranch();
        final GetActiveNodeVersionsParams params = GetActiveNodeVersionsParams.create().nodeId( nodeId ).
            branches( Branches.from( branch ) ).
            build();
        return this.nodeService.getActiveVersions( params ).
            getNodeVersions().
            get( branch );
    }

    private NodeVersionQueryResult getVersions( final NodeId nodeId )
    {
        final NodeVersionQuery.Builder queryBuilder = NodeVersionQuery.create().
            nodeId( nodeId ).
            size( this.maxVersions != null ? this.maxVersions : -1 );

        if ( this.maxAge != null )
        {
            final Value ageValue = ValueFactory.newDateTime( Instant.now().minus( Duration.ofDays( this.maxAge ) ) );
            queryBuilder.addQueryFilter( RangeFilter.create().
                from( ageValue ).
                build() );
        }

        return this.nodeService.findVersions( queryBuilder.build() );
    }

    private void reportDumpingBranch( final Branch branch, final Long totalHits )
    {
        if ( this.listener != null )
        {
            this.listener.dumpingBranch( this.repositoryId, branch, totalHits );
        }
        else
        {
            LOG.info( "Dumping repository [" + this.repositoryId + "], branch [" + branch + "]  " );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private boolean includeVersions;

        private boolean includeBinaries;

        private NodeService nodeService;

        private RepositoryService repositoryService;

        private DumpWriter writer;

        private Integer maxAge;

        private Integer maxVersions;

        private SystemDumpListener listener;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder writer( final DumpWriter writer )
        {
            this.writer = writer;
            return this;
        }

        public Builder maxAge( final Integer maxAge )
        {
            this.maxAge = maxAge;
            return this;
        }

        public Builder maxVersions( final Integer maxVersions )
        {
            this.maxVersions = maxVersions;
            return this;
        }

        public Builder listener( final SystemDumpListener listener )
        {
            this.listener = listener;
            return this;
        }

        public RepoDumper build()
        {
            return new RepoDumper( this );
        }
    }
}
