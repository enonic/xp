package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.ClusterProviderId;
import com.enonic.xp.cluster.ClusterValidationError;

public class ProviderHealthError
    implements ClusterValidationError
{
    private final ClusterProviderId providerId;

    public ProviderHealthError( final ClusterProviderId providerId )
    {
        this.providerId = providerId;
    }

    @Override
    public String getMessage()
    {
        return "Provider " + providerId + " not healthy";
    }
}
