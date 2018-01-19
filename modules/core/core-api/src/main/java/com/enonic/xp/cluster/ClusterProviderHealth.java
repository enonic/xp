package com.enonic.xp.cluster;

public enum ClusterProviderHealth
{
    YELLOW, GREEN, RED;

    public boolean isHealthy()
    {
        return this.equals( YELLOW ) || this.equals( GREEN );
    }
}
