package com.enonic.xp.elasticsearch.impl.status.cluster;

final class ClusterHealth
{
    private final String clusterHealthStatus;

    private final String errorMessage;

    private ClusterHealth( Builder builder )
    {
        this.clusterHealthStatus = builder.clusterHealthStatus;
        this.errorMessage = builder.errorMessage;
    }

    String getClusterHealthStatus()
    {
        return clusterHealthStatus;
    }

    String getErrorMessage()
    {
        return errorMessage;
    }

    static ClusterHealth.Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String clusterHealthStatus;

        private String errorMessage;

        private Builder()
        {
        }

        Builder clusterHealthStatus( final String clusterHealthStatus )
        {
            this.clusterHealthStatus = clusterHealthStatus;
            return this;
        }

        Builder errorMessage( final String errorMessage )
        {
            this.errorMessage = errorMessage;
            return this;
        }

        ClusterHealth build()
        {
            return new ClusterHealth( this );
        }
    }

}
