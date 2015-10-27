package com.enonic.xp.node;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Beta
public class PushNodesResult
{
    private final Nodes successful;

    private final ImmutableSet<Failed> failed;

    private PushNodesResult( Builder builder )
    {
        successful = Nodes.from( builder.successful );
        failed = ImmutableSet.copyOf( builder.failed );
    }

    public Nodes getSuccessful()
    {
        return successful;
    }


    public ImmutableSet<Failed> getFailed()
    {
        return failed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<Node> successful = Lists.newLinkedList();

        private final List<Failed> failed = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder addSuccess( final Node success )
        {
            this.successful.add( success );
            return this;
        }

        public Builder addFailed( final Node failed, final Reason reason )
        {
            this.failed.add( new Failed( failed, reason ) );
            return this;
        }

        public PushNodesResult build()
        {
            return new PushNodesResult( this );
        }
    }

    public enum Reason
    {
        PARENT_NOT_FOUND,
        ACCESS_DENIED
    }

    public static final class Failed
    {
        private final Node node;

        private final Reason reason;

        public Failed( final Node node, final Reason reason )
        {
            this.node = node;
            this.reason = reason;
        }

        public Node getNode()
        {
            return node;
        }

        public Reason getReason()
        {
            return reason;
        }
    }
}
