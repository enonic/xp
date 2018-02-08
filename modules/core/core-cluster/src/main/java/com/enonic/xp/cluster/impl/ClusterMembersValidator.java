package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviders;
import com.enonic.xp.cluster.ClusterValidator;
import com.enonic.xp.cluster.ClusterValidatorResult;

public class ClusterMembersValidator
    implements ClusterValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    public ClusterValidatorResult validate( final ClusterProviders providers )
    {
        ClusterNodes current = null;
        ClusterProvider first = null;

        for ( final ClusterProvider provider : providers )
        {
            final ClusterNodes providerNodes = provider.getNodes();

            if ( first != null && current != null && !current.equals( providerNodes ) )
            {
                final NodesMismatchError error = new NodesMismatchError( provider, first, providerNodes, current );

                LOG.error( error.getMessage() );

                return ClusterValidatorResult.create().
                    ok( false ).
                    error( error ).
                    build();
            }

            if ( first == null )
            {
                first = provider;
            }

            if ( current == null )
            {
                current = providerNodes;
            }
        }

        return ClusterValidatorResult.ok();
    }


}
