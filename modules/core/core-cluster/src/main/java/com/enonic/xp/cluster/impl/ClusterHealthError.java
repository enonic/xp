package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterValidationError;

class ClusterHealthError
    implements ClusterValidationError
{
    private final ClusterId providerId;

    private final String errorMessage;

    ClusterHealthError( final ClusterId providerId, final String errorMessage )
    {
        this.providerId = providerId;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage()
    {
        return "Provider " + providerId + " not healthy" + ( errorMessage == null ? "" : ": " + errorMessage );
    }
}
