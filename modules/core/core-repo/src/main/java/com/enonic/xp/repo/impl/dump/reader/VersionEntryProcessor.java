package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public class VersionEntryProcessor
    extends AbstractEntryProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private final static Logger LOG = LoggerFactory.getLogger( VersionEntryProcessor.class );

    private VersionEntryProcessor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final EntryLoadResult.Builder result = EntryLoadResult.create();

        final VersionsDumpEntry nodeVersionsEntry = this.serializer.toNodeVersionsEntry( line );

        final Set<VersionMeta> versions = nodeVersionsEntry.getVersions();

        addVersions( result, nodeVersionsEntry, versions );

        this.result = result.build();
        return true;
    }

    private void addVersions( final EntryLoadResult.Builder result, final VersionsDumpEntry versionsDumpEntry,
                              final Set<VersionMeta> versions )
    {
        for ( final VersionMeta version : versions )
        {
            final NodeVersion nodeVersion = getVersion( version );

            if ( nodeVersion == null )
            {
                reportVersionError( result, version );
                return;
            }

            try
            {
                this.nodeService.importNodeVersion( ImportNodeVersionParams.create().
                    nodeId( versionsDumpEntry.getNodeId() ).
                    timestamp( version.getTimestamp() ).
                    nodePath( version.getNodePath() ).
                    nodeVersion( nodeVersion ).
                    build() );

                validateOrAddBinary( nodeVersion, result );
                result.successful();
            }
            catch ( Exception e )
            {
                result.error( EntryLoadError.error(
                    String.format( "Cannot load version with id %s, path %s: %s", versionsDumpEntry.getNodeId(), version.getNodePath(),
                                   e.getMessage() ) ) );
            }
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
        public VersionEntryProcessor build()
        {
            return new VersionEntryProcessor( this );
        }
    }
}
