package com.enonic.xp.core.impl.audit;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.audit.CleanUpAuditLogListener;
import com.enonic.xp.audit.CleanUpAuditLogResult;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;

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
        listener = builder.listener;
    }

    @Override
    public CleanUpAuditLogResult execute()
    {
        if ( Instant.EPOCH.equals( until ) )
        {
            LOG.debug( "ageThreshold hasn't been set, no need to clean up logs" );
            return CleanUpAuditLogResult.empty();

        }
        return AuditLogContext.createContext().callWith( this::doCleanUp );
    }

    private CleanUpAuditLogResult doCleanUp()
    {
        final CleanUpAuditLogResult.Builder result = CleanUpAuditLogResult.create();

        final NodeQuery query = createQuery();

        FindNodesByQueryResult nodesToDelete = nodeService.findByQuery( query );

        long hits = nodesToDelete.getHits();
        final long totalHits = nodesToDelete.getTotalHits();

        if ( totalHits == 0 )
        {
            return CleanUpAuditLogResult.empty();
        }

        listener.start( BATCH_SIZE );

        while ( hits > 0 )
        {
            for ( NodeHit nodeHit : nodesToDelete.getNodeHits() )
            {
                result.deleted( nodeService.deleteById( nodeHit.getNodeId() ).getSize() );

                listener.processed();
            }

            nodesToDelete = nodeService.findByQuery( query );

            hits = nodesToDelete.getHits();
        }

        listener.finished();

        return result.build();
    }

    private NodeQuery createQuery()
    {
        final NodeQuery.Builder builder = NodeQuery.create().
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.NODE_TYPE.toString() ).
                addValue( ValueFactory.newString( AuditLogConstants.NODE_TYPE.toString() ) ).
                build() );

        final RangeFilter timeToFilter = RangeFilter.create().
            fieldName( AuditLogConstants.TIME.toString() ).
            to( ValueFactory.newDateTime( until ) ).
            build();
        builder.addQueryFilter( timeToFilter );

        builder.addOrderBy( FieldOrderExpr.create( AuditLogConstants.TIME, OrderExpr.Direction.ASC ) ).
            size( BATCH_SIZE );

        return builder.
            build();
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
            Preconditions.checkNotNull( ageThreshold, "ageThreshold cannot be null" );
            Preconditions.checkNotNull( listener, "listener cannot be null" );
        }

        public CleanUpAuditLogCommand build()
        {
            validate();
            return new CleanUpAuditLogCommand( this );
        }
    }
}
