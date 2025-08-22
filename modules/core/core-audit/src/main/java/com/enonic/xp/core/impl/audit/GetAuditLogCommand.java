package com.enonic.xp.core.impl.audit;

import java.util.Objects;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.core.impl.audit.serializer.AuditLogSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;

public class GetAuditLogCommand
    extends NodeServiceCommand<AuditLog>
{
    private final AuditLogId auditLogId;

    private GetAuditLogCommand( final Builder builder )
    {
        super( builder );
        auditLogId = builder.auditLogId;
    }

    @Override
    public AuditLog execute()
    {
        return AuditLogContext.createContext().callWith( this::doExecute );
    }

    private AuditLog doExecute()
    {
        final NodeId nodeId = NodeId.from( auditLogId );
        try
        {
            final Node node = nodeService.getById( nodeId );
            return AuditLogSerializer.fromNode( node );
        }
        catch ( NodeNotFoundException e )
        {
            return null;
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private AuditLogId auditLogId;

        private Builder()
        {
        }

        public Builder auditLogId( final AuditLogId val )
        {
            auditLogId = val;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( auditLogId, "auditLogId is required" );
        }

        public GetAuditLogCommand build()
        {
            validate();
            return new GetAuditLogCommand( this );
        }
    }
}
