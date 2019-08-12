package com.enonic.xp.core.impl.auditlog;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogs;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.filter.IdFilter;

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
        FindNodesByQueryResult result = nodeService.findByQuery( createQuery() );
        Nodes nodes = nodeService.getByIds( result.getNodeIds() );

        List<AuditLog> logs = nodes.stream().map( AuditLogSerializer::fromNode ).collect( Collectors.toList() );

        return FindAuditLogResult.create().
            hits( AuditLogs.from( logs ) ).
            build();
    }

    private NodeQuery createQuery()
    {
        NodeQuery.Builder builder = NodeQuery.create();
        if ( params.getIds() != null )
        {
            builder.addQueryFilter( IdFilter.create().
                values( params.getIds().asStrings() ).
                build() );
        }
        return builder.build();
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
