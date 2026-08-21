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

    private CleanUpAuditLogResult doCleanUp()
    {
        final CleanUpAuditLogResult.Builder result = CleanUpAuditLogResult.create();

        final EnumerateNodesParams.Builder enumeration =
            EnumerateNodesParams.create().parentPath( NodePath.ROOT ).modifiedBefore( until );

        boolean started = false;
        int deleted = 0;
        int resolved = -1;
        String cursor = null;

        do
        {
            final EnumerateNodesParams params = enumeration.cursor( cursor ).build();
            final EnumerateNodesResult batch = nodeService.enumerate( params );

            if ( resolved != deleted + batch.getRemaining() )
            {
                resolved = deleted + batch.getRemaining();
                listener.resolved( resolved );
            }

            for ( final NodeEnumerationEntry entry : batch.getEntries() )
            {
                if ( !started )
                {
                    listener.start( params.getBatchSize() );
                    started = true;
                }

                result.deleted(
                    nodeService.delete( DeleteNodeParams.create().nodeId( entry.nodeId() ).build() ).getNodeIds().getSize() );
                deleted++;

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
