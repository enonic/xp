package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

public class BranchEntryProcessor
    extends AbstractEntryProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private static final Logger LOG = LoggerFactory.getLogger( BranchEntryProcessor.class );

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

        addNode( result, meta );

        this.result = result.build();
        return true;
    }

    private void addNode( final EntryLoadResult.Builder result, final VersionMeta meta )
    {
        final NodeStoreVersion nodeVersion = getVersion( meta );

        if ( nodeVersion == null )
        {
            reportVersionError( result, meta );
            return;
        }

        try
        {
            final Node node = NodeFactory.create( nodeVersion, meta.version(), meta.nodePath(),
                                                  meta.timestamp().truncatedTo( ChronoUnit.MILLIS ) );

            this.nodeService.loadNode(
                LoadNodeParams.create().node( node ).nodeCommitId( meta.nodeCommitId() ).attributes( meta.attributes() ).build() );

            addBinary( nodeVersion, result );

            result.successful();
        }
        catch ( Exception e )
        {
            final String message =
                String.format( "Cannot load node with id %s, path %s: %s", nodeVersion.id(), meta.nodePath(), e.getMessage() );
            result.error( EntryLoadError.error( message ) );
            LOG.error( message, e );
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
