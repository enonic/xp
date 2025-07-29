package com.enonic.xp.core.impl.audit;

import com.google.common.base.Preconditions;

import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogs;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.core.impl.audit.serializer.AuditLogSerializer;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKey;

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
        return AuditLogContext.createContext().callWith( this::runQuery );
    }

    private FindAuditLogResult runQuery()
    {
        final NodeQuery query = createQuery();

        if ( query == null )
        {
            return FindAuditLogResult.empty();
        }

        FindNodesByQueryResult result = nodeService.findByQuery( query );

        AuditLogs logs = result.getNodeIds().
            stream().
            map( nodeService::getById ).
            map( AuditLogSerializer::fromNode ).
            collect( AuditLogs.collector() );

        return FindAuditLogResult.create().
            total( result.getTotalHits() ).
            hits( logs ).
            build();
    }

    private NodeQuery createQuery()
    {
        final NodeQuery.Builder builder = NodeQuery.create().
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.NODE_TYPE.toString() ).
                addValue( ValueFactory.newString( AuditLogConstants.NODE_TYPE.toString() ) ).
                build() );

        if ( params.getIds() != null )
        {
            builder.addQueryFilter( IdFilter.create().
                values( params.getIds() ).
                build() );
        }

        if ( params.getFrom() != null || params.getTo() != null )
        {
            RangeFilter.Builder fb = RangeFilter.create();
            fb.fieldName( AuditLogPropertyNames.TIME );
            if ( params.getFrom() != null )
            {
                fb.from( ValueFactory.newDateTime( params.getFrom() ) );
            }
            if ( params.getTo() != null )
            {
                fb.to( ValueFactory.newDateTime( params.getTo() ) );
            }
            builder.addQueryFilter( fb.build() );
        }

        if ( params.getSource() != null )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( AuditLogPropertyNames.SOURCE ).
                addValue( ValueFactory.newString( params.getSource() ) ).
                build() );
        }

        if ( params.getType() != null )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( AuditLogPropertyNames.TYPE ).
                addValue( ValueFactory.newString( params.getType() ) ).
                build() );
        }

        if ( params.getUsers() != null )
        {
            final ValueFilter.Builder filter = ValueFilter.create().
                fieldName( AuditLogPropertyNames.USER );
            params.getUsers().stream().
                map( PrincipalKey::toString ).
                map( ValueFactory::newString ).
                forEach( filter::addValue );
            builder.addQueryFilter( filter.build() );
        }

        if ( params.getObjectUris() != null )
        {
            final ValueFilter.Builder filter = ValueFilter.create().
                fieldName( AuditLogPropertyNames.OBJECTURIS );
            params.getObjectUris().stream().
                map( AuditLogUri::toString ).
                map( ValueFactory::newString ).
                forEach( filter::addValue );
            builder.addQueryFilter( filter.build() );
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
