package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterValidationWarning;

class ClusterHealthWarning
    implements ClusterValidationWarning
{
    private final ClusterId providerId;

    private final String errorMessage;

    ClusterHealthWarning( final ClusterId providerId, final String errorMessage )
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
