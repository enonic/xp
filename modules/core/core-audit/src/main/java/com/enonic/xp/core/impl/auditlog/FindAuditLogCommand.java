package com.enonic.xp.core.impl.auditlog;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogs;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class FindAuditLogCommand
    extends NodeServiceCommand<FindAuditLogResult>
{
    private final FindAuditLogParams params;

    private FindAuditLogCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    @Override
    public FindAuditLogResult execute()
    {
        return AuditLogContext.createContext().callWith( () -> runQuery() );
    }

    private FindAuditLogResult runQuery()
    {
        final NodeQuery query = createQuery();

        if ( query == null )
        {
            return FindAuditLogResult.empty();
        }

        FindNodesByQueryResult result = nodeService.findByQuery( query );
        Nodes nodes = nodeService.getByIds( result.getNodeIds() );

        List<AuditLog> logs = nodes.stream().map( AuditLogSerializer::fromNode ).collect( Collectors.toList() );

        return FindAuditLogResult.create().
            total( result.getTotalHits() ).
            hits( AuditLogs.from( logs ) ).
            build();
    }

    private NodeQuery createQuery()
    {
        final NodeQuery.Builder builder = NodeQuery.create();
        final AtomicBoolean filterAdded = new AtomicBoolean( false );

        Consumer<Filter> addFilter = ( f ) -> {
            builder.addQueryFilter( f );
            filterAdded.set( true );
        };

        if ( params.getIds() != null )
        {
            addFilter.accept( IdFilter.create().
                values( params.getIds().asStrings() ).
                build() );
        }

        if ( params.getFrom() != null || params.getTo() != null )
        {
            RangeFilter.Builder fb = RangeFilter.create();
            fb.fieldName( "time" );
            if ( params.getFrom() != null )
            {
                fb.from( ValueFactory.newDateTime( params.getFrom() ) );
            }
            if ( params.getTo() != null )
            {
                fb.to( ValueFactory.newDateTime( params.getTo() ) );
            }
            addFilter.accept( fb.build() );
        }

        if ( params.getSource() != null )
        {
            addFilter.accept( ValueFilter.create().
                fieldName( "source" ).
                addValue( ValueFactory.newString( params.getSource() ) ).
                build() );
        }

        if ( params.getType() != null )
        {
            addFilter.accept( ValueFilter.create().
                fieldName( "type" ).
                addValue( ValueFactory.newString( params.getType() ) ).
                build() );
        }

        if ( !filterAdded.get() )
        {
            return null;
        }

        return builder.
            from( params.getStart() ).
            size( params.getCount() ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private FindAuditLogParams params;

        private Builder()
        {
        }

        public Builder params( final FindAuditLogParams val )
        {
            params = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( params, "FindAuditLogParams params cannot be null" );
        }

        public FindAuditLogCommand build()
        {
            validate();
            return new FindAuditLogCommand( this );
        }
    }
}
