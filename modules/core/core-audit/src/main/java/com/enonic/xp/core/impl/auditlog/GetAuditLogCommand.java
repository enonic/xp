package com.enonic.xp.core.impl.auditlog;

import com.google.common.base.Preconditions;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

public class GetAuditLogCommand
    extends NodeServiceCommand<AuditLog>
{
    private final AuditLogId id;

    private GetAuditLogCommand( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    @Override
    public AuditLog execute()
    {
        Node node = AuditLogContext.createContext().callWith( () -> nodeService.getById( NodeId.from( id ) ) );
        return AuditLogSerializer.fromNode( node );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private AuditLogId id;

        private Builder()
        {
        }

        public Builder id( final AuditLogId val )
        {
            id = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( id, "AuditLogId cannot be null" );
        }

        public GetAuditLogCommand build()
        {
            validate();
            return new GetAuditLogCommand( this );
        }
    }
}
