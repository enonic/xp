package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterValidationError;

class ClusterHealthError
    implements ClusterValidationError
{
    private final ClusterId providerId;

    ClusterHealthError( final ClusterId providerId )
    {
        this.providerId = providerId;
    }

    @Override
    public String getMessage()
    {
        return "Provider " + providerId + " not healthy";
    }
}
