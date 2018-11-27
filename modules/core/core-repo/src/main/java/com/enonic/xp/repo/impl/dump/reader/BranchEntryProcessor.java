package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

public class BranchEntryProcessor
    extends AbstractEntryProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private final static Logger LOG = LoggerFactory.getLogger( BranchEntryProcessor.class );

    private BranchEntryProcessor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final EntryLoadResult.Builder result = EntryLoadResult.create();

        final BranchDumpEntry branchDumpEntry = this.serializer.toBranchMetaEntry( line );

        final VersionMeta meta = branchDumpEntry.getMeta();

        addNode( result, branchDumpEntry, meta );

        this.result = result.build();
        return true;
    }

    private void addNode( final EntryLoadResult.Builder result, final BranchDumpEntry branchDumpEntry, final VersionMeta meta )
    {
        final NodeVersion nodeVersion = getVersion( meta );

        if ( nodeVersion == null )
        {
            reportVersionError( result, meta );
            return;
        }

        final Node node = NodeFactory.create( nodeVersion, NodeBranchEntry.create().
            nodeId( branchDumpEntry.getNodeId() ).
            nodePath( meta.getNodePath() ).
            timestamp( nodeVersion.getTimestamp() ).
            nodeState( meta.getNodeState() ).
            build() );

        try
        {
            this.nodeService.loadNode( LoadNodeParams.create().
                node( node ).
                build() );

            validateOrAddBinary( nodeVersion, result );

            result.successful();
        }
        catch ( Exception e )
        {
            result.error( EntryLoadError.error(
                String.format( "Cannot load node with id %s, path %s: %s", node.id(), node.path(), e.getMessage() ) ) );
        }
    }

    private NodeVersion getVersion( final VersionMeta meta )
    {
        try
        {
            return this.dumpReader.get( repositoryId, meta.getVersion() );
        }
        catch ( RepoLoadException e )
        {
            LOG.error( "Cannot load version, missing in existing blobStore, and not present in dump: " + meta.getVersion(), e );
            return null;
        }
    }

    @Override
    public EntryLoadResult getResult()
    {
        return result;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractEntryProcessor.Builder<Builder>
    {
        public BranchEntryProcessor build()
        {
            return new BranchEntryProcessor( this );
        }
    }
}
