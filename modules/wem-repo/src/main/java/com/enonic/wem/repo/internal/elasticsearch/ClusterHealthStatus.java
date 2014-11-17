package com.enonic.wem.repo.internal.elasticsearch;

public class ClusterHealthStatus
{
    private final ClusterStatusCode clusterStatusCode;

    private final boolean timedOut;

    public ClusterHealthStatus( final ClusterStatusCode clusterStatusCode, final boolean timedOut )
    {
        this.clusterStatusCode = clusterStatusCode;
        this.timedOut = timedOut;
    }

    public ClusterStatusCode getClusterStatusCode()
    {
        return clusterStatusCode;
    }

    public boolean isTimedOut()
    {
        return timedOut;
    }
}
