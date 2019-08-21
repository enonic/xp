package com.enonic.xp.core.impl.auditlog;

import com.google.common.base.Preconditions;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.core.impl.auditlog.serializer.AuditLogSerializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

public class CreateAuditLogCommand
    extends NodeServiceCommand<AuditLog>
{
    private final AuditLogParams params;

    private CreateAuditLogCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    @Override
    public AuditLog execute()
    {
        CreateNodeParams createNodeParams = AuditLogSerializer.toCreateNodeParams( params ).
            parent( NodePath.ROOT ).
            childOrder( AuditLogConstants.AUDIT_LOG_REPO_DEFAULT_CHILD_ORDER ).
            build();

        Node createdNode = AuditLogContext.createAdminContext().callWith( () -> {
            Node n = nodeService.create( createNodeParams );
            nodeService.refresh( RefreshMode.ALL );
            return n;
        } );

        return AuditLogSerializer.fromNode( createdNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private AuditLogParams params;

        private Builder()
        {
        }

        public Builder params( final AuditLogParams val )
        {
            params = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( params, "AuditLogParams cannot be null" );
        }

        public CreateAuditLogCommand build()
        {
            validate();
            return new CreateAuditLogCommand( this );
        }
    }
}
