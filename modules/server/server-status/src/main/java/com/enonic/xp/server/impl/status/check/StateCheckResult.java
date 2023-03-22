package com.enonic.xp.server.impl.status.check;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class StateCheckResult
{
    private final List<String> errorMessages;

    public StateCheckResult( final Builder builder )
    {
        this.errorMessages = builder.errorMessages.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<String> errorMessages = ImmutableList.builder();

        private Builder()
        {

        }

        public Builder addErrorMessage( final String errorMessage )
        {
            this.errorMessages.add( errorMessage );
            return this;
        }

        public StateCheckResult build()
        {
            return new StateCheckResult( this );
        }
    }
}
