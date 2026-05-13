package com.enonic.xp.repo.impl.dump.reader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;

public class VersionEntryProcessor
    extends AbstractEntryProcessor
    implements EntryProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( VersionEntryProcessor.class );

    private final boolean includeVersions;

    private final Map<Branch, BranchLoadResult.Builder> branchResults = new LinkedHashMap<>();

    private final EntryLoadResult.Builder resultBuilder = EntryLoadResult.create();

    private VersionEntryProcessor( final Builder builder )
    {
        super( builder );
        this.includeVersions = builder.includeVersions;
    }

    @Override
    public void processLine( final String line )
    {
        if ( line.isBlank() )
        {
            return;
        }
        final DumpSerializer.NodeVersionLine versionLine = this.serializer.toNodeVersionLine( line );
        final NodeId nodeId = NodeId.from( versionLine.nodeId() );
        final List<Branch> branches = versionLine.branches() == null
            ? List.of()
            : versionLine.branches().stream().map( Branch::from ).toList();
        processVersion( resultBuilder, nodeId, versionLine.version(), branches );
    }

    private void processVersion( final EntryLoadResult.Builder entryResult, final NodeId nodeId, final VersionMeta version,
                                 final List<Branch> activeBranches )
    {
        final NodeStoreVersion nodeVersion = getVersion( version );

        if ( nodeVersion == null )
        {
            reportVersionError( entryResult, version );
            return;
        }

        try
        {
            final Node node = NodeFactory.create( nodeVersion )
                .id( nodeId )
                .timestamp( version.timestamp() )
                .parentPath( version.nodePath().getParentPath() )
                .name( version.nodePath().getName() )
                .nodeVersionId( version.version() )
                .build();

            if ( includeVersions )
            {
                this.nodeLoader.importNodeVersion( ImportNodeVersionParams.create()
                                                       .node( node )
                                                       .nodeCommitId( version.nodeCommitId() )
                                                       .attributes( version.attributes() )
                                                       .build() );
            }

            for ( Branch branch : activeBranches )
            {
                loadIntoBranch( branch, node, version );
            }

            addBinary( nodeVersion, entryResult );
            entryResult.successful();
        }
        catch ( Exception e )
        {
            final String message =
                String.format( "Cannot load version with id %s, path %s: %s", nodeId, version.nodePath(), e.getMessage() );
            entryResult.error( EntryLoadError.error( message ) );
            LOG.error( message, e );
        }
    }

    private void loadIntoBranch( final Branch branch, final Node node, final VersionMeta version )
    {
        final BranchLoadResult.Builder branchResult = branchResults.computeIfAbsent( branch, BranchLoadResult::create );
        try
        {
            ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( repositoryId )
                .branch( branch )
                .build()
                .runWith( () -> this.nodeLoader.loadNode( LoadNodeParams.create()
                                                              .node( node )
                                                              .nodeCommitId( version.nodeCommitId() )
                                                              .attributes( version.attributes() )
                                                              .build() ) );
            branchResult.successful( branchResult.build().getSuccessful() + 1 );
        }
        catch ( Exception e )
        {
            LOG.error( "Cannot load node {} into branch {}: {}", node.id(), branch, e.getMessage(), e );
        }
    }

    private NodeStoreVersion getVersion( final VersionMeta meta )
    {
        try
        {
            return this.dumpReader.get( repositoryId, meta.nodeVersionKey() );
        }
        catch ( RepoLoadException e )
        {
            LOG.error( "Cannot load version, missing in existing blobStore, and not present in dump: {}", meta.version(), e );
            return null;
        }
    }

    @Override
    public EntryLoadResult getResult()
    {
        return resultBuilder.build();
    }

    public Map<Branch, BranchLoadResult> getBranchLoadResults()
    {
        final Map<Branch, BranchLoadResult> built = new LinkedHashMap<>();
        branchResults.forEach( ( branch, builder ) -> built.put( branch, builder.build() ) );
        return built;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractEntryProcessor.Builder<Builder>
    {
        private boolean includeVersions;

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public VersionEntryProcessor build()
        {
            return new VersionEntryProcessor( this );
        }
    }
}
