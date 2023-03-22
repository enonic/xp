package com.enonic.xp.status.health;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class HealthCheckResult
{
    private final List<String> errorMessages;

    public HealthCheckResult( final Builder builder )
    {
        this.errorMessages = builder.errorMessages.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean isHealthy()
    {
        return errorMessages.isEmpty();
    }

    public boolean isNotHealthy()
    {
        return !errorMessages.isEmpty();
    }

    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    public static final class Builder
    {

        private final ImmutableList.Builder<String> errorMessages = ImmutableList.builder();

        public Builder addErrorMessage( final String errorMessage )
        {
            this.errorMessages.add( errorMessage );
            return this;
        }

        public HealthCheckResult build()
        {
            return new HealthCheckResult( this );
        }
    }
}
