package com.enonic.xp.cluster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProvider;

class ClusterMembersValidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ClusterMembersValidator.class );

    static boolean validate( final ClusterProviders providers )
    {
        ClusterNodes current = null;
        ClusterProvider first = null;

        for ( final ClusterProvider provider : providers )
        {
            final ClusterNodes providerNodes = provider.getNodes();

            if ( first != null && current != null && !current.equals( providerNodes ) )
            {
                LOG.error( nodesErrorString( first, provider, current, providerNodes ) );
                return false;
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

        return true;
    }

    private static String nodesErrorString( final ClusterProvider p1, final ClusterProvider p2, final ClusterNodes c1,
                                            final ClusterNodes c2 )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "ClusterNodes not matching: " );
        builder.append( p1.getId() + ": " + c1 );
        builder.append( "; " );
        builder.append( p2.getId() + ": " + c2 );

        return builder.toString();
    }

}
