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
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeEnumerationEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
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

import static java.util.Objects.requireNonNull;
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

    /**
     * Every node observed by the branch scans, with the version each branch's scan observed it at - so the version dumping binds a
     * branch to the version the scan saw, not to whatever is active by the time the versions are dumped.
     */
    private final Map<NodeId, Map<Branch, NodeVersionId>> nodesToDump = new LinkedHashMap<>();

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
            // nodes, versions and commits are all read from storage, so the search index has nothing to contribute to a dump
            this.nodeService.refresh( RefreshMode.STORAGE );
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
                // enumerated from storage: a dump answers for what the repository holds, not for what the search index has caught up with
                long branchNodeCount = 0;
                String cursor = null;
                do
                {
                    final EnumerateNodesResult batch = this.nodeService.enumerate( EnumerateNodesParams.create()
                                                                                       .parentPath( NodePath.ROOT )
                                                                                       .batchSize( EnumerateNodesParams.MAX_BATCH_SIZE )
                                                                                       .cursor( cursor )
                                                                                       .build() );
                    for ( final NodeEnumerationEntry entry : batch.getEntries() )
                    {
                        if ( nodeIds == null || nodeIds.contains( entry.nodeId() ) )
                        {
                            nodesToDump.computeIfAbsent( entry.nodeId(), key -> new LinkedHashMap<>() )
                                .put( branch, entry.versionId() );
                            branchNodeCount++;
                        }
                    }
                    cursor = batch.getCursor();
                }
                while ( cursor != null );

                this.listener.dumpingBranch( repositoryId, branch, branchNodeCount + 1 );
                LOG.info( "Visiting repository [{}], branch [{}]", repositoryId, branch );

                nodesToDump.computeIfAbsent( NodeId.ROOT, key -> new LinkedHashMap<>() )
                    .put( branch, this.nodeService.getById( NodeId.ROOT ).getNodeVersionId() );
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
            for ( Map.Entry<NodeId, Map<Branch, NodeVersionId>> nodeToDump : nodesToDump.entrySet() )
            {
                final NodeId nodeId = nodeToDump.getKey();
                final Set<NodeVersionId> written = new HashSet<>();
                try (DumpWriter.VersionsStream stream = writer.openVersions( nodeId ))
                {
                    final Map<NodeVersionId, List<Branch>> branchesByVersion = new LinkedHashMap<>();
                    nodeToDump.getValue()
                        .forEach( ( branch, versionId ) -> branchesByVersion.computeIfAbsent( versionId, _ -> new ArrayList<>() )
                            .add( branch ) );

                    for ( Branch activeBranch : nodeToDump.getValue().keySet() )
                    {
                        final BranchDumpResult.Builder branchResult = branchResults.get( activeBranch );
                        if ( branchResult != null )
                        {
                            branchResult.addedNode();
                        }
                        this.listener.nodeDumped();
                    }

                    for ( Map.Entry<NodeVersionId, List<Branch>> versionBranches : branchesByVersion.entrySet() )
                    {
                        final NodeVersion nodeVersion = requireNonNull( this.nodeService.getVersion( nodeId, versionBranches.getKey() ),
                                                                        "Version " + versionBranches.getKey() + " of node " + nodeId +
                                                                            " not found" );
                        written.add( nodeVersion.getNodeVersionId() );
                        stream.append( VersionMetaFactory.create( nodeVersion ), versionBranches.getValue() );
                        doStoreVersion( nodeVersion, this.dumpResult );
                        this.dumpResult.addedVersion();
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
