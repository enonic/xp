package com.enonic.xp.repo.impl.dump;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.Millis;
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
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;

public class RepoDumper
{
    private static final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final RepositoryId repositoryId;

    private final Branches branches;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final NodeService nodeService;

    private final DumpWriter writer;

    private final RepoDumpResult.Builder dumpResult;

    private final NodeIds nodeIds;

    private final SystemDumpListener listener;

    private RepoDumper( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branches = builder.branches;
        this.includeVersions = builder.includeVersions;
        this.includeBinaries = builder.includeBinaries;
        this.nodeService = builder.nodeService;
        this.writer = builder.writer;
        this.dumpResult = RepoDumpResult.create( this.repositoryId );
        this.maxAge = builder.maxAge;
        this.maxVersions = builder.maxVersions;
        this.nodeIds = builder.nodeIds;
        this.listener = Objects.requireNonNullElse( builder.listener, NoopSystemDumpListener.INSTANCE );
    }

    public RepoDumpResult execute()
    {
        final Set<NodeId> dumpedNodes = new HashSet<>();

        final Consumer<NodeId> nodeIdsAccumulator = includeVersions ? dumpedNodes::add : nodeId -> {
        };

        setContext( RepositoryConstants.MASTER_BRANCH ).runWith( () -> {
            this.nodeService.refresh( RefreshMode.ALL );
            for ( Branch branch : this.branches )
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
            writer.openBranchMeta( repositoryId, branch );
            try
            {
                final FindNodesByParentResult children = this.nodeService.findByParent(
                    FindNodesByParentParams.create().parentId( NodeId.ROOT ).recursive( true ).childOrder( ChildOrder.path() ).build() );

                final NodeIds nodesToDump = nodeIds != null
                    ? children.getNodeIds().stream().filter( nodeIds::contains ).collect( NodeIds.collector() )
                    : children.getNodeIds();

                this.listener.dumpingBranch( repositoryId, branch, nodesToDump.getSize() + 1 );
                LOG.info( "Dumping repository [{}], branch [{}]", repositoryId, branch );

                doDumpNode( NodeId.ROOT, branch, branchDumpResult );
                nodeIdsAccumulator.accept( NodeId.ROOT );

                for ( final NodeId child : nodesToDump )
                {
                    doDumpNode( child, branch, branchDumpResult );
                    nodeIdsAccumulator.accept( child );
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot fully dump repository [{}] branch [{}]", repositoryId, branch, e );
                branchDumpResult.error(
                    DumpError.error( "Cannot fully dump repository [" + repositoryId + "] branch [" + branch + "]: " + e.getMessage() ) );
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
        writer.openVersionsMeta( repositoryId );
        try
        {
            for ( NodeId nodeId : dumpedNodes )
            {
                final List<VersionMeta> versionMetas = new ArrayList<>();

                final NodeVersionQueryResult versions = getVersions( nodeId );
                for ( final NodeVersion nodeVersion : versions.getNodeVersions() )
                {
                    versionMetas.add( VersionMetaFactory.create( nodeVersion ) );
                    doStoreVersion( nodeVersion, this.dumpResult );
                    this.dumpResult.addedVersion();
                }

                writer.writeVersionsEntry( new VersionsDumpEntry( nodeId, versionMetas ) );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Cannot fully dump repository [{}] versions", repositoryId, e );
            dumpResult.error( DumpError.error( "Cannot fully dump repository [" + repositoryId + "] versions: " + e.getMessage() ) );
        }
        finally
        {
            writer.closeMeta();
        }
    }

    private void dumpCommits()
    {
        writer.openCommitsMeta( repositoryId );
        try
        {
            final NodeCommitQuery nodeCommitQuery = NodeCommitQuery.create().size( -1 ).build();

            final NodeCommitEntries nodeCommitEntries = this.nodeService.findCommits( nodeCommitQuery ).getNodeCommitEntries();

            nodeCommitEntries.stream()
                .map( nodeCommitEntry -> new CommitDumpEntry( nodeCommitEntry.getNodeCommitId(), nodeCommitEntry.getMessage(),
                                                              nodeCommitEntry.getTimestamp(), nodeCommitEntry.getCommitter() ) )
                .forEach( writer::writeCommitEntry );
        }
        finally
        {
            writer.closeMeta();
        }
    }

    private void doStoreVersion( final NodeVersion nodeVersion, final RepoDumpResult.Builder dumpResult )
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
            writer.writeNodeVersionBlobs( repositoryId, nodeVersionKey );
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
                this.writer.writeBinaryBlob( repositoryId, attachedBinary );
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
        return ContextBuilder.from( ContextAccessor.current() ).repositoryId( repositoryId ).branch( branch ).build();
    }

    private void doDumpNode( final NodeId nodeId, final Branch branch, final BranchDumpResult.Builder dumpResult )
    {
        try
        {
            final BranchDumpEntry branchDumpEntry = createDumpEntry( nodeId, branch );
            writer.writeBranchEntry( branchDumpEntry );
            writer.writeNodeVersionBlobs( repositoryId, branchDumpEntry.meta().nodeVersionKey() );
            writeBinaries( dumpResult, branchDumpEntry.binaryReferences() );
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
                writer.writeBinaryBlob( repositoryId, BlobKey.from( ref ) );
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
        final Node currentNode = this.nodeService.getById( nodeId );

        final NodeVersion currentVersion = this.nodeService.getActiveVersions(
                GetActiveNodeVersionsParams.create().nodeId( nodeId ).branches( Branches.from( branch ) ).build() )
            .getNodeVersions()
            .get( branch );

        final VersionMeta meta = VersionMetaFactory.create( currentVersion );

        final List<String> binaryReferences =
            this.includeBinaries ? currentNode.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).toList() : List.of();

        return new BranchDumpEntry( nodeId, meta, binaryReferences );
    }

    private NodeVersionQueryResult getVersions( final NodeId nodeId )
    {
        final NodeVersionQuery.Builder queryBuilder =
            NodeVersionQuery.create().nodeId( nodeId ).size( this.maxVersions != null ? this.maxVersions : -1 );

        if ( this.maxAge != null )
        {
            final Value ageValue = ValueFactory.newDateTime( Millis.now().minus( Duration.ofDays( this.maxAge ) ) );
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
        private RepositoryId repositoryId;

        private Branches branches;

        private boolean includeVersions;

        private boolean includeBinaries;

        private NodeService nodeService;

        private DumpWriter writer;

        private Integer maxAge;

        private Integer maxVersions;

        private NodeIds nodeIds;

        private SystemDumpListener listener;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder branches( final Branches val )
        {
            branches = val;
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

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
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
