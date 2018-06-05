package com.enonic.xp.cluster;

import java.util.Objects;

public class ClusterHealth
{
    private final ClusterHealthStatus status;

    private final String errorMessage;

    private ClusterHealth( Builder builder )
    {
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
    }

    public ClusterHealthStatus getStatus()
    {
        return status;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isHealthy()
    {
        return status != ClusterHealthStatus.RED;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ClusterHealth that = (ClusterHealth) o;
        return status == that.status && Objects.equals( errorMessage, that.errorMessage );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( status, errorMessage );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static ClusterHealth green()
    {
        return create().status( ClusterHealthStatus.GREEN ).build();
    }

    public static ClusterHealth yellow()
    {
        return create().status( ClusterHealthStatus.YELLOW ).build();
    }

    public static ClusterHealth red()
    {
        return create().status( ClusterHealthStatus.RED ).build();
    }

    public static class Builder
    {
        private ClusterHealthStatus status;

        private String errorMessage;

        private Builder()
        {
        }

        public Builder status( final ClusterHealthStatus status )
        {
            this.status = status;
            return this;
        }

        public Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        public ClusterHealth build()
        {
            return new ClusterHealth( this );
        }
    }
}
