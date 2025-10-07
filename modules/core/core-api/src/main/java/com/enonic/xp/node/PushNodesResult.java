package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PushNodesResult
{
    private final ImmutableList<PushNodeResult> successful;

    private final ImmutableList<PushNodeResult> failed;

    private PushNodesResult( Builder builder )
    {
        successful = builder.successful.build();
        failed = builder.failed.build();
    }

    public List<PushNodeResult> getSuccessful()
    {
        return successful;
    }

    public List<PushNodeResult> getFailed()
    {
        return failed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<PushNodeResult> successful = ImmutableList.builder();

        private final ImmutableList.Builder<PushNodeResult> failed = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final PushNodeResult pushNodeResult )
        {
            if ( pushNodeResult.getFailureReason() == null )
            {
                successful.add( pushNodeResult );

            }
            else
            {
                failed.add( pushNodeResult );
            }
            return this;
        }

        public PushNodesResult build()
        {
            return new PushNodesResult( this );
        }
    }
}
