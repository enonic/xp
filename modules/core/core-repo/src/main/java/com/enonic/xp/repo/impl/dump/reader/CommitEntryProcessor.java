package com.enonic.xp.repo.impl.dump.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.node.ImportNodeCommitParams;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;

public class CommitEntryProcessor
    extends AbstractEntryProcessor
    implements EntryProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( CommitEntryProcessor.class );

    private final EntryLoadResult.Builder resultBuilder = EntryLoadResult.create();

    private CommitEntryProcessor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public void processLine( final String line )
    {
        if ( line.isBlank() )
        {
            return;
        }
        final CommitDumpEntry commitDumpEntry = this.serializer.toCommitDumpEntry( line );
        addCommit( resultBuilder, commitDumpEntry );
    }

    private void addCommit( final EntryLoadResult.Builder result, final CommitDumpEntry commitDumpEntry )
    {
        try
        {
            ImportNodeCommitParams params = ImportNodeCommitParams.create()
                .nodeCommitId( commitDumpEntry.nodeCommitId() )
                .message( commitDumpEntry.message() )
                .committer( commitDumpEntry.committer() )
                .timestamp( commitDumpEntry.timestamp() )
                .build();

            this.nodeLoader.importNodeCommit( params );
            result.successful();
        }
        catch ( Exception e )
        {
            final String message = String.format( "Cannot load commit with id %s: %s", commitDumpEntry.nodeCommitId(), e.getMessage() );
            result.error( EntryLoadError.error( message ) );
            LOG.error( message, e );
        }
    }

    @Override
    public EntryLoadResult getResult()
    {
        return resultBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractEntryProcessor.Builder<Builder>
    {
        public CommitEntryProcessor build()
        {
            return new CommitEntryProcessor( this );
        }
    }
}
