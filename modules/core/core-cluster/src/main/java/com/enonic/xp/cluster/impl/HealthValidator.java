package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviders;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;

public class HealthValidator
    implements ClusterValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    @Override
    public ClusterValidatorResult validate( final ClusterProviders providers )
    {
        for ( final ClusterProvider provider : providers )
        {
            if ( !provider.getHealth().isHealthy() )
            {
                final ProviderHealthError error = new ProviderHealthError( provider.getId() );

                LOG.error( error.getMessage() );

                return ClusterValidatorResult.create().
                    error( error ).
                    build();
            }
        }

        return ClusterValidatorResult.ok();
    }
}
