package com.enonic.xp.core.impl.audit;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.NodeEnumerationEntry;
import com.enonic.xp.node.NodePath;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

public class CleanUpAuditLogCommand
    extends NodeServiceCommand<CleanUpAuditLogResult>
{
    private static final Logger LOG = LoggerFactory.getLogger( CleanUpAuditLogCommand.class );

    private final Instant until;

    private final CleanUpAuditLogListener listener;

    private CleanUpAuditLogCommand( final Builder builder )
    {
        super( builder );
        until = builder.ageThreshold.isBlank() ? Instant.EPOCH : Instant.now().minus( Duration.parse( builder.ageThreshold ) );
        listener = requireNonNullElseGet( builder.listener, EmptyCleanUpAuditLogListener::new );
    }

    @Override
    public CleanUpAuditLogResult execute()
    {
        if ( Instant.EPOCH.equals( until ) )
        {
            LOG.debug( "ageThreshold hasn't been set, no need to clean up logs" );
            return CleanUpAuditLogResult.create().build();

        }
        return AuditLogContext.createContext().callWith( this::doCleanUp );
    }

    /**
     * Enumerates the expired records from storage in batches - the enumeration itself is bounded by the age threshold, so the scan costs
     * what expires rather than what the log holds, and every record it answers with is deleted, oldest first. Records are aged by the
     * moment they were written: the log is add-only, so a record's timestamp never changes. No refresh is needed anywhere: the cursor
     * only moves forward over ground the deletions leave behind, and a record too fresh to be visible without one is far too fresh to
     * fall under the threshold.
     */
    private CleanUpAuditLogResult doCleanUp()
    {
        final CleanUpAuditLogResult.Builder result = CleanUpAuditLogResult.create();

        boolean started = false;
        int resolved = 0;
        String cursor = null;

        do
        {
            final EnumerateNodesResult batch = nodeService.enumerate( EnumerateNodesParams.create()
                                                                           .parentPath( NodePath.ROOT )
                                                                           .modifiedBefore( until )
                                                                           .cursor( cursor )
                                                                           .build() );

            if ( !batch.getEntries().isEmpty() )
            {
                // what the clean-up knows it has to delete so far: the enumeration answers only with expired records, but it cannot say
                // how many are still ahead of the cursor, so the total grows with every batch instead of being known up front
                resolved += batch.getEntries().size();
                listener.resolved( resolved );
            }

            for ( final NodeEnumerationEntry entry : batch.getEntries() )
            {
                if ( !started )
                {
                    listener.start( EnumerateNodesParams.MAX_BATCH_SIZE );
                    started = true;
                }

                result.deleted(
                    nodeService.delete( DeleteNodeParams.create().nodeId( entry.nodeId() ).build() ).getNodeIds().getSize() );

                listener.processed();
            }

            cursor = batch.getCursor();
        }
        while ( cursor != null );

        if ( started )
        {
            listener.finished();
        }

        return result.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private String ageThreshold;

        private CleanUpAuditLogListener listener;

        private Builder()
        {
        }

        public Builder ageThreshold( final String value )
        {
            ageThreshold = value;
            return this;
        }

        public Builder listener( final CleanUpAuditLogListener value )
        {
            listener = value;
            return this;
        }

        private void validate()
        {
            requireNonNull( ageThreshold, "ageThreshold is required" );
        }

        public CleanUpAuditLogCommand build()
        {
            validate();
            return new CleanUpAuditLogCommand( this );
        }
    }

    private static class EmptyCleanUpAuditLogListener
        implements CleanUpAuditLogListener
    {
        @Override
        public void start( final int batchSize )
        {
        }

        @Override
        public void processed()
        {
        }

        @Override
        public void finished()
        {
        }
    }
}
