package com.enonic.xp.repo.impl.dump;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.NodeVersionKey;
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
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;

public class RepoDumper
{
    private static final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final Repository repository;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final NodeService nodeService;

    private final DumpWriter writer;

    private final RepoDumpResult.Builder dumpResult;

    private final SystemDumpListener listener;

    private RepoDumper( final Builder builder )
    {
        this.repository = builder.repository;
        this.includeVersions = builder.includeVersions;
        this.includeBinaries = builder.includeBinaries;
        this.nodeService = builder.nodeService;
        this.writer = builder.writer;
        this.dumpResult = RepoDumpResult.create( this.repository.getId() );
        this.maxAge = builder.maxAge;
        this.maxVersions = builder.maxVersions;
        this.listener = Objects.requireNonNullElseGet( builder.listener, NullSystemDumpListener::new );
    }

    public RepoDumpResult execute()
    {
        final Set<NodeId> dumpedNodes = new HashSet<>();

        final Consumer<NodeId> nodeIdsAccumulator = includeVersions ? dumpedNodes::add : nodeId -> {
        };

        for ( Branch branch : this.repository.getBranches() )
        {
            setContext( branch ).runWith( () -> dumpBranch( nodeIdsAccumulator ) );
        }

        if ( includeVersions )
        {
            setContext( RepositoryConstants.MASTER_BRANCH ).runWith( () -> dumpVersions( dumpedNodes ) );
        }

        setContext( RepositoryConstants.MASTER_BRANCH ).runWith( this::dumpCommits );

        return this.dumpResult.build();
    }

    private void dumpBranch( Consumer<NodeId> nodeIdsAccumulator )
    {
        this.nodeService.refresh( RefreshMode.ALL );

        final Branch branch = ContextAccessor.current().getBranch();

        final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( branch );
        writer.openBranchMeta( repository.getId(), branch );
        try
        {
            try
            {
                dumpBranch( branchDumpResult, nodeIdsAccumulator );
            }
            catch ( Exception e )
            {
                throw new RepoDumpException( "Error occurred when dumping repository " + repository.getId(), e );
            }
            finally
            {
                writer.closeMeta();
            }
        }
        catch ( RepoDumpException e )
        {
            LOG.error( "Cannot fully dump repository [{}] branch [{}]",repository.getId(), branch, e );
            branchDumpResult.error(
                DumpError.error( "Cannot fully dump repository [" + repository.getId() + "] branch [" + branch + "]: " + e.getMessage() ) );
        }
        this.dumpResult.add( branchDumpResult.build() );
    }

    private void dumpBranch( final BranchDumpResult.Builder dumpResult, Consumer<NodeId> nodeIdsAccumulator )
    {
        final FindNodesByParentResult children = this.nodeService.findByParent(
            FindNodesByParentParams.create().parentId( Node.ROOT_UUID ).recursive( true ).childOrder( ChildOrder.path() ).build() );

        final Branch branch = ContextAccessor.current().getBranch();
        this.listener.dumpingBranch( repository.getId(), branch, children.getTotalHits() + 1 );
        LOG.info( "Dumping repository [{}], branch [{}]", repository.getId(), branch );

        doDumpNode( Node.ROOT_UUID, dumpResult );
        nodeIdsAccumulator.accept( Node.ROOT_UUID );

        for ( final NodeId child : children.getNodeIds() )
        {
            doDumpNode( child, dumpResult );
            nodeIdsAccumulator.accept( child );
        }
    }

    private void dumpVersions( final Collection<NodeId> dumpedNodes )
    {
        writer.openVersionsMeta( repository.getId() );
        try
        {
            try
            {
                for ( NodeId nodeId : dumpedNodes )
                {
                    final VersionsDumpEntry.Builder builder = VersionsDumpEntry.create( nodeId );

                    final NodeVersionQueryResult versions = getVersions( nodeId );
                    for ( final NodeVersionMetadata metaData : versions.getNodeVersionsMetadata() )
                    {
                        doStoreVersion( builder, metaData, this.dumpResult );
                        this.dumpResult.addedVersion();
                    }

                    writer.writeVersionsEntry( builder.build() );
                }
            }
            finally
            {
                writer.closeMeta();
            }
        }
        catch ( RepoDumpException e )
        {
            LOG.error( "Cannot fully dump repository [{}] versions",repository.getId(), e );
            dumpResult.error(
                DumpError.error( "Cannot fully dump repository [" + repository.getId() + "] versions: " + e.getMessage() ) );
        }
    }

    private void dumpCommits()
    {
        writer.openCommitsMeta( repository.getId() );
        try
        {
            final NodeCommitQuery nodeCommitQuery = NodeCommitQuery.create().size( -1 ).build();

            final NodeCommitEntries nodeCommitEntries = this.nodeService.findCommits( nodeCommitQuery ).
                getNodeCommitEntries();

            nodeCommitEntries.stream().
                map( nodeCommitEntry -> CommitDumpEntry.create().
                    nodeCommitId( nodeCommitEntry.getNodeCommitId() ).
                    message( nodeCommitEntry.getMessage() ).
                    committer( nodeCommitEntry.getCommitter() ).
                    timestamp( nodeCommitEntry.getTimestamp() ).
                    build() ).
                forEach( writer::writeCommitEntry );
        }
        finally
        {
            writer.closeMeta();
        }
    }

    private void doStoreVersion( final VersionsDumpEntry.Builder builder, final NodeVersionMetadata metaData,
                                 final RepoDumpResult.Builder dumpResult )
    {
        try
        {
            final NodeVersion nodeVersion = this.nodeService.getByNodeVersionKey( metaData.getNodeVersionKey() );
            builder.addVersion( VersionMetaFactory.create( metaData ) );

            storeVersionBlob( metaData.getNodeVersionId(), metaData.getNodeVersionKey() );
            storeVersionBinaries( metaData.getNodeVersionId(), nodeVersion );
        }
        catch ( Exception e )
        {
            dumpResult.error( DumpError.error( e.getMessage() ) );
        }
    }

    private void storeVersionBlob( final NodeVersionId nodeVersionId, final NodeVersionKey nodeVersionKey )
    {
        try
        {
            writer.writeNodeVersionBlobs( repository.getId(), nodeVersionKey );
        }
        catch ( Exception e )
        {
            // Report
            LOG.error( "Failed to write version for nodeVersion " + nodeVersionId, e );
        }
    }

    private void storeVersionBinaries( final NodeVersionId nodeVersionId, final NodeVersion nodeVersion )
    {
        nodeVersion.getAttachedBinaries().forEach( ( attachedBinary ) -> {
            try
            {
                this.writer.writeBinaryBlob( repository.getId(), BlobKey.from( attachedBinary.getBlobKey() ) );
            }
            catch ( Exception e )
            {
                // Report
                LOG.error( "Failed to write binary for nodeVersion " + nodeVersionId + ", binary " + attachedBinary.getBlobKey(), e );
            }
        } );
    }

    private Context setContext( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( branch ).
            build();
    }

    private void doDumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        try
        {
            final BranchDumpEntry branchDumpEntry = createDumpEntry( nodeId );
            writer.writeBranchEntry( branchDumpEntry );
            writer.writeNodeVersionBlobs( repository.getId(), branchDumpEntry.getMeta().getNodeVersionKey() );
            writeBinaries( dumpResult, branchDumpEntry.getBinaryReferences() );
            dumpResult.addedNode();
            this.listener.nodeDumped();
        }
        catch ( Exception e )
        {
            dumpResult.error( DumpError.error( "Cannot dump node with id [" + nodeId + "]: " + e.getMessage() ) );
        }
    }

    private void writeBinaries( final BranchDumpResult.Builder dumpResult, final Collection<String> binaryReferences )
    {
        binaryReferences.forEach( ref -> {
            try
            {
                writer.writeBinaryBlob( repository.getId(), BlobKey.from( ref ) );
            }
            catch ( RepoDumpException e )
            {
                LOG.error( "Cannot dump binary:", e );
                dumpResult.error( DumpError.error( "Cannot dump binary: " + e.getMessage() ) );
            }
        } );
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
                    fieldName( VersionIndexPath.TIMESTAMP.getPath() ).
                from( ageValue ).
                build() );
        }

        return this.nodeService.findVersions( queryBuilder.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Repository repository;

        private boolean includeVersions;

        private boolean includeBinaries;

        private NodeService nodeService;

        private DumpWriter writer;

        private Integer maxAge;

        private Integer maxVersions;

        private SystemDumpListener listener;

        private Builder()
        {
        }

        public Builder repository( final Repository val )
        {
            repository = val;
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
