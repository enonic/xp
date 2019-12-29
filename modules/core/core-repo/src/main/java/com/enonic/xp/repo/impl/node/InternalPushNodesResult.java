package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodesResult;

public class InternalPushNodesResult
    extends PushNodesResult
{
    private final PushNodeEntries pushNodeEntries;

    protected InternalPushNodesResult( Builder builder )
    {
        super( builder );
        pushNodeEntries = builder.pushNodeEntries;
    }

    public PushNodeEntries getPushNodeEntries()
    {
        return pushNodeEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends PushNodesResult.Builder<Builder>
    {
        private PushNodeEntries pushNodeEntries;

        private Builder()
        {
        }

        public Builder setPushNodeEntries( final PushNodeEntries pushNodeEntries )
        {
            this.pushNodeEntries = pushNodeEntries;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( pushNodeEntries );
        }

        @Override
        public InternalPushNodesResult build()
        {
            validate();
            return new InternalPushNodesResult( this );
        }
    }
}
