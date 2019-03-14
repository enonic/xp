package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.ImportNodeCommitParams;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;

public class CommitEntryProcessor
    extends AbstractEntryProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private CommitEntryProcessor( final Builder builder )
    {
        super( builder );
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final EntryLoadResult.Builder result = EntryLoadResult.create();

        final CommitDumpEntry commitDumpEntry = this.serializer.toCommitDumpEntry( line );

        addCommit( result, commitDumpEntry );

        this.result = result.build();
        return true;
    }

    private void addCommit( final EntryLoadResult.Builder result, final CommitDumpEntry commitDumpEntry )
    {
        try
        {
            ImportNodeCommitParams params = ImportNodeCommitParams.create().
                nodeCommitId( commitDumpEntry.getNodeCommitId() ).
                message( commitDumpEntry.getMessage() ).
                committer( commitDumpEntry.getCommitter() ).
                timestamp( commitDumpEntry.getTimestamp() ).
                build();

            this.nodeService.importNodeCommit( params );
            result.successful();
        }
        catch ( Exception e )
        {
            result.error( EntryLoadError.error(
                String.format( "Cannot load commit with id %s: %s", commitDumpEntry.getNodeCommitId(), e.getMessage() ) ) );
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
        public CommitEntryProcessor build()
        {
            return new CommitEntryProcessor( this );
        }
    }
}
