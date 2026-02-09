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
import com.enonic.xp.blob.BlobKeys;
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
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersion;
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
import com.enonic.xp.repository.RepositoryId;

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
        this.listener = Objects.requireNonNullElse( builder.listener, NoopSystemDumpListener.INSTANCE );
    }

    public RepoDumpResult execute()
    {
        final Set<NodeId> dumpedNodes = new HashSet<>();

        final Consumer<NodeId> nodeIdsAccumulator = includeVersions ? dumpedNodes::add : nodeId -> {
        };

        setContext( RepositoryConstants.MASTER_BRANCH ).runWith( () -> {
            this.nodeService.refresh( RefreshMode.ALL );
            for ( Branch branch : this.repository.getBranches() )
            {
                dumpBranch( branch, nodeIdsAccumulator );
            }
            if ( includeVersions )
            {
                dumpVersions( dumpedNodes );
            }
            dumpCommits();
        } );

        return this.dumpResult.build();
    }

    private void dumpBranch( final Branch branch, Consumer<NodeId> nodeIdsAccumulator )
    {
        setContext( branch ).runWith( () -> {
            final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( branch );
            writer.openBranchMeta( repository.getId(), branch );
            try
            {
                final FindNodesByParentResult children = this.nodeService.findByParent(
                    FindNodesByParentParams.create().parentId( Node.ROOT_UUID ).recursive( true ).childOrder( ChildOrder.path() ).build() );

                this.listener.dumpingBranch( repository.getId(), branch, children.getTotalHits() + 1 );
                LOG.info( "Dumping repository [{}], branch [{}]", repository.getId(), branch );

                doDumpNode( Node.ROOT_UUID, branch, branchDumpResult );
                nodeIdsAccumulator.accept( Node.ROOT_UUID );

                for ( final NodeId child : children.getNodeIds() )
                {
                    doDumpNode( child, branch, branchDumpResult );
                    nodeIdsAccumulator.accept( child );
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot fully dump repository [{}] branch [{}]", repository.getId(), branch, e );
                branchDumpResult.error( DumpError.error(
                    "Cannot fully dump repository [" + repository.getId() + "] branch [" + branch + "]: " + e.getMessage() ) );
            }
            finally
            {
                writer.closeMeta();
            }

            this.dumpResult.add( branchDumpResult.build() );
        } );
    }

    private void dumpVersions( final Collection<NodeId> dumpedNodes )
    {
        writer.openVersionsMeta( repository.getId() );
        try
        {
            for ( NodeId nodeId : dumpedNodes )
            {
                final VersionsDumpEntry.Builder builder = VersionsDumpEntry.create( nodeId );

                final NodeVersionQueryResult versions = getVersions( nodeId );
                for ( final NodeVersion nodeVersion : versions.getNodeVersions() )
                {
                    builder.addVersion( VersionMetaFactory.create( nodeVersion ) );
                    doStoreVersion( nodeVersion, this.dumpResult );
                    this.dumpResult.addedVersion();
                }

                writer.writeVersionsEntry( builder.build() );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Cannot fully dump repository [{}] versions", repository.getId(), e );
            dumpResult.error( DumpError.error( "Cannot fully dump repository [" + repository.getId() + "] versions: " + e.getMessage() ) );
        }
        finally
        {
            writer.closeMeta();
        }
    }

    private void dumpCommits()
    {
        writer.openCommitsMeta( repository.getId() );
        try
        {
            final NodeCommitQuery nodeCommitQuery = NodeCommitQuery.create().size( -1 ).build();

            final NodeCommitEntries nodeCommitEntries = this.nodeService.findCommits( nodeCommitQuery ).getNodeCommitEntries();

            nodeCommitEntries.stream()
                .map( nodeCommitEntry -> CommitDumpEntry.create()
                    .nodeCommitId( nodeCommitEntry.getNodeCommitId() )
                    .message( nodeCommitEntry.getMessage() )
                    .committer( nodeCommitEntry.getCommitter() )
                    .timestamp( nodeCommitEntry.getTimestamp() )
                    .build() )
                .forEach( writer::writeCommitEntry );
        }
        finally
        {
            writer.closeMeta();
        }
    }

    private void doStoreVersion( final NodeVersion nodeVersion,
                                 final RepoDumpResult.Builder dumpResult )
    {
        try
        {
            storeVersionBlob( nodeVersion.getNodeVersionId(), nodeVersion.getNodeVersionKey() );
            storeVersionBinaries( nodeVersion.getNodeVersionId(), nodeVersion.getBinaryBlobKeys() );
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

    private void storeVersionBinaries( final NodeVersionId nodeVersionId, final BlobKeys attachedBinaries )
    {
        attachedBinaries.forEach( ( attachedBinary ) -> {
            try
            {
                this.writer.writeBinaryBlob( repository.getId(), attachedBinary );
            }
            catch ( Exception e )
            {
                // Report
                LOG.error( "Failed to write binary for nodeVersion " + nodeVersionId + ", binary " + attachedBinary, e );
            }
        } );
    }

    private Context setContext( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).repositoryId( repository.getId() ).branch( branch ).build();
    }

    private void doDumpNode( final NodeId nodeId, final Branch branch, final BranchDumpResult.Builder dumpResult )
    {
        try
        {
            final BranchDumpEntry branchDumpEntry = createDumpEntry( nodeId, branch );
            writer.writeBranchEntry( branchDumpEntry );
            writer.writeNodeVersionBlobs( repository.getId(), branchDumpEntry.getMeta().nodeVersionKey() );
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

    private BranchDumpEntry createDumpEntry( final NodeId nodeId, final Branch branch )
    {
        final BranchDumpEntry.Builder builder = BranchDumpEntry.create().nodeId( nodeId );

        final Node currentNode = this.nodeService.getById( nodeId );

        final NodeVersion currentVersion = this.nodeService.getActiveVersions(
                GetActiveNodeVersionsParams.create().nodeId( nodeId ).branches( Branches.from( branch ) ).build() )
            .getNodeVersions()
            .get( branch );

        builder.meta( VersionMetaFactory.create( currentVersion ) );

        if ( this.includeBinaries )
        {
            builder.setBinaryReferences(
                currentNode.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toList() ) );
        }

        return builder.build();
    }

    private NodeVersionQueryResult getVersions( final NodeId nodeId )
    {
        final NodeVersionQuery.Builder queryBuilder =
            NodeVersionQuery.create().nodeId( nodeId ).size( this.maxVersions != null ? this.maxVersions : -1 );

        if ( this.maxAge != null )
        {
            final Value ageValue = ValueFactory.newDateTime( Instant.now().minus( Duration.ofDays( this.maxAge ) ) );
            queryBuilder.addQueryFilter( RangeFilter.create().fieldName( VersionIndexPath.TIMESTAMP.getPath() ).from( ageValue ).build() );
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

    public enum NoopSystemDumpListener
        implements SystemDumpListener
    {
        INSTANCE;

        @Override
        public void totalBranches( final long total )
        {
        }

        @Override
        public void dumpingBranch( final RepositoryId repositoryId, final Branch branch, final long total )
        {
        }

        @Override
        public void nodeDumped()
        {
        }
    }
}
