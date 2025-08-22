package com.enonic.xp.core.impl.audit;

import java.util.Objects;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.serializer.AuditLogSerializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class CreateAuditLogCommand
    extends NodeServiceCommand<AuditLog>
{
    private final LogAuditLogParams params;

    private CreateAuditLogCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    @Override
    public AuditLog execute()
    {
        return AuditLogContext.createContext().callWith( () -> {
            Node createdNode = createNode();
            return AuditLogSerializer.fromNode( createdNode );
        } );
    }

    private Node createNode()
    {
        NodeId id = new NodeId();

        CreateNodeParams createNodeParams = AuditLogSerializer.toCreateNodeParams( params ).
            setNodeId( id ).
            name( id.toString() ).
            parent( NodePath.ROOT ).
            childOrder( AuditLogConstants.AUDIT_LOG_REPO_DEFAULT_CHILD_ORDER ).
            build();
        return nodeService.create( createNodeParams );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends NodeServiceCommand.Builder<Builder>
    {
        private LogAuditLogParams params;

        private Builder()
        {
        }

        public Builder params( final LogAuditLogParams val )
        {
            params = val;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public CreateAuditLogCommand build()
        {
            validate();
            return new CreateAuditLogCommand( this );
        }
    }
}
