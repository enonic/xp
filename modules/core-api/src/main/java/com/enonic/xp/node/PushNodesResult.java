package com.enonic.xp.node;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PushNodesResult
{
    private final Nodes successfull;

    private final ImmutableSet<Failed> failed;

    private PushNodesResult( Builder builder )
    {
        successfull = Nodes.from( builder.successfull );
        failed = ImmutableSet.copyOf( builder.failed );
    }

    public Nodes getSuccessfull()
    {
        return successfull;
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
        private final Set<Node> successfull = Sets.newHashSet();

        private final Set<Failed> failed = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder addSuccess( final Node success )
        {
            this.successfull.add( success );
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
