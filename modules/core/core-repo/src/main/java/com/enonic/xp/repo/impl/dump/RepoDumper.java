package com.enonic.xp.repo.impl.dump;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
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
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;

import static java.util.Objects.requireNonNullElse;

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

    private final Set<NodeId> nodesToDump = new HashSet<>();

    private final Map<Branch, BranchDumpResult.Builder> branchResults = new LinkedHashMap<>();

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
        this.listener = requireNonNullElse( builder.listener, NoopSystemDumpListener.INSTANCE );
    }

    public RepoDumpResult execute()
    {
        nodesToDump.clear();
        branchResults.clear();
        for ( Branch branch : this.branches )
        {
            branchResults.put( branch, BranchDumpResult.create( branch ) );
        }

        setContext( RepositoryConstants.MASTER_BRANCH ).runWith( () -> {
            this.nodeService.refresh( RefreshMode.ALL );
            for ( Branch branch : this.branches )
            {
                visitBranch( branch );
            }
            dumpVersions();
            dumpCommits();
        } );

        branchResults.values().forEach( b -> this.dumpResult.add( b.build() ) );
        return this.dumpResult.build();
    }

    private void visitBranch( final Branch branch )
    {
        setContext( branch ).runWith( () -> {
            final BranchDumpResult.Builder branchDumpResult = branchResults.get( branch );
            try
            {
                final FindNodesByParentResult children = this.nodeService.findByParent(
                    FindNodesByParentParams.create().parentId( NodeId.ROOT ).recursive( true ).childOrder( ChildOrder.path() ).build() );

                final NodeIds branchNodes = nodeIds != null
                    ? children.getNodeIds().stream().filter( nodeIds::contains ).collect( NodeIds.collector() )
                    : children.getNodeIds();

                this.listener.dumpingBranch( repositoryId, branch, branchNodes.getSize() + 1 );
                LOG.info( "Visiting repository [{}], branch [{}]", repositoryId, branch );

                nodesToDump.add( NodeId.ROOT );
                branchNodes.forEach( nodesToDump::add );
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot fully dump repository [{}] branch [{}]", repositoryId, branch, e );
                branchDumpResult.error(
                    DumpError.error( "Cannot fully dump repository [" + repositoryId + "] branch [" + branch + "]: " + e.getMessage() ) );
            }
        } );
    }

    private void dumpVersions()
    {
        writer.openVersionsMeta( repositoryId );
        try
        {
            for ( NodeId nodeId : nodesToDump )
            {
                final Set<NodeVersionId> written = new HashSet<>();
                try (DumpWriter.VersionsStream stream = writer.openVersions( nodeId ))
                {
                    final GetActiveNodeVersionsResult activeVersions = this.nodeService.getActiveVersions(
                        GetActiveNodeVersionsParams.create().nodeId( nodeId ).branches( this.branches ).build() );

                    final Map<NodeVersionId, List<Branch>> branchesByVersion = new LinkedHashMap<>();
                    final Map<NodeVersionId, NodeVersion> activeByVersion = new LinkedHashMap<>();
                    activeVersions.getNodeVersions().forEach( ( branch, nodeVersion ) -> {
                        activeByVersion.putIfAbsent( nodeVersion.getNodeVersionId(), nodeVersion );
                        branchesByVersion.computeIfAbsent( nodeVersion.getNodeVersionId(), _ -> new ArrayList<>() ).add( branch );
                    } );

                    for ( Branch activeBranch : activeVersions.getNodeVersions().keySet() )
                    {
                        final BranchDumpResult.Builder branchResult = branchResults.get( activeBranch );
                        if ( branchResult != null )
                        {
                            branchResult.addedNode();
                        }
                        this.listener.nodeDumped();
                    }

                    for ( NodeVersion nodeVersion : activeByVersion.values() )
                    {
                        if ( written.add( nodeVersion.getNodeVersionId() ) )
                        {
                            stream.append( VersionMetaFactory.create( nodeVersion ),
                                           branchesByVersion.get( nodeVersion.getNodeVersionId() ) );
                            doStoreVersion( nodeVersion, this.dumpResult );
                            this.dumpResult.addedVersion();
                        }
                    }

                    if ( includeVersions )
                    {
                        for ( NodeVersion historyVersion : getVersions( nodeId ).getNodeVersions() )
                        {
                            if ( written.add( historyVersion.getNodeVersionId() ) )
                            {
                                stream.append( VersionMetaFactory.create( historyVersion ), List.of() );
                                doStoreVersion( historyVersion, this.dumpResult );
                                this.dumpResult.addedVersion();
                            }
                        }
                    }
                }
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
            if ( this.includeBinaries )
            {
                storeVersionBinaries( nodeVersion.getNodeVersionId(), nodeVersion.getBinaryBlobKeys() );
            }
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
