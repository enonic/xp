package com.enonic.xp.elasticsearch.impl.status.cluster;

public final class ClusterHealth
{
    private String clusterHealthStatus;

    private String errorMessage;

    private ClusterHealth( Builder builder )
    {
        this.clusterHealthStatus = builder.clusterHealthStatus;
        this.errorMessage = builder.errorMessage;
    }

    public String getClusterHealthStatus()
    {
        return clusterHealthStatus;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public static ClusterHealth.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String clusterHealthStatus;

        private String errorMessage;


        private Builder()
        {
        }

        public Builder clusterHealthStatus( final String clusterHealthStatus )
        {
            this.clusterHealthStatus = clusterHealthStatus;
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
