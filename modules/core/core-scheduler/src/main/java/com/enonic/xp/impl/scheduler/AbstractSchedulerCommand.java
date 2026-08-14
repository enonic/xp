package com.enonic.xp.impl.scheduler;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.scheduler.ScheduledJob;

import static java.util.Objects.requireNonNull;

public abstract class AbstractSchedulerCommand
{
    protected final NodeService nodeService;

    protected <B extends Builder<B>> AbstractSchedulerCommand( final Builder<B> builder )
    {
        this.nodeService = builder.nodeService;
    }

    protected ScheduledJob toScheduledJob( final Node node )
    {
        final NodeVersion version = node.getNodeVersionId() != null ? nodeService.getVersion( node.id(), node.getNodeVersionId() ) : null;
        return SchedulerSerializer.fromNode( node, version != null ? version.getAttributes() : null );
    }

    public abstract static class Builder<B extends Builder<B>>
    {
        private NodeService nodeService;

        protected Builder()
        {
        }

        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        protected void validate()
        {
            requireNonNull( nodeService );
        }

        abstract AbstractSchedulerCommand build();
    }
}
