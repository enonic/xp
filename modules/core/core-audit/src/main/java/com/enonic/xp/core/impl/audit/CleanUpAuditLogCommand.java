package com.enonic.xp.core.impl.audit;

import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

public class CleanUpAuditLogCommand
    extends NodeServiceCommand<CleanUpAuditLogResult>
{
    private static final Logger LOG = LoggerFactory.getLogger( CleanUpAuditLogCommand.class );

    private static final int BATCH_SIZE = 10_000;

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
     * Enumerates the audit log from storage in batches and deletes every record older than the age threshold, aged by the moment it was
     * written. The repository holds nothing but audit log records, and the cursor only moves forward over ground the deletions leave
     * behind, so one storage refresh up front is the only refresh the whole clean-up needs.
     */
    private CleanUpAuditLogResult doCleanUp()
    {
        final CleanUpAuditLogResult.Builder result = CleanUpAuditLogResult.create();

        nodeService.refresh( RefreshMode.STORAGE );

        boolean started = false;
        String cursor = null;

        do
        {
            final ListNodesResult batch = nodeService.list(
                ListNodesParams.create().parentPath( NodePath.ROOT ).batchSize( BATCH_SIZE ).cursor( cursor ).build() );

            for ( final NodeListEntry entry : batch.getEntries() )
            {
                if ( entry.timestamp().isBefore( until ) )
                {
                    if ( !started )
                    {
                        listener.start( BATCH_SIZE );
                        started = true;
                    }

                    result.deleted(
                        nodeService.delete( DeleteNodeParams.create().nodeId( entry.nodeId() ).build() ).getNodeIds().getSize() );

                    listener.processed();
                }
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
