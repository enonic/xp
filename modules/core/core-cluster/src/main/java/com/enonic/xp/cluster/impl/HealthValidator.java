package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterProvider;

class HealthValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    static boolean validate( final ClusterProviders providers )
    {
        for ( final ClusterProvider provider : providers )
        {
            if ( !provider.getHealth().isHealthy() )
            {
                LOG.error( "Provider " + provider.getId() + " not healthy" );
                return false;
            }

        }

        return true;
    }
}
