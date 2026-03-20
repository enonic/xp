package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.scheduler.SchedulerConstants;

public class SchedulerUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( SchedulerUpgrader.class );

    private static final List<String> TIME_PROPERTIES = List.of( "createdTime", "modifiedTime" );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !SchedulerConstants.SCHEDULER_REPO_ID.equals( repositoryId ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();

        if ( data.getInstant( "createdTime" ) == null )
        {
            // this can't be a scheduler node, so we skip it
            return null;
        }

        boolean dataModified = false;

        for ( String propertyName : TIME_PROPERTIES )
        {
            final Instant value = data.getInstant( propertyName );
            if ( value != null )
            {
                final Instant truncated = value.truncatedTo( ChronoUnit.MILLIS );
                if ( !truncated.equals( value ) )
                {
                    data.setInstant( propertyName, truncated );
                    dataModified = true;
                }
            }
        }

        if ( dataModified )
        {
            LOG.info( "Truncated time properties to millis for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        }

        final boolean nodeTypeChanged = !SchedulerConstants.NODE_TYPE.equals( nodeVersion.nodeType() );

        if ( nodeTypeChanged )
        {
            LOG.info( "Changed nodeType to [{}] for node [{}] in repository [{}]", SchedulerConstants.NODE_TYPE, nodeVersion.id(),
                      repositoryId );
        }

        if ( !dataModified && !nodeTypeChanged )
        {
            return null;
        }
        return NodeStoreVersion.create( nodeVersion ).nodeType( SchedulerConstants.NODE_TYPE ).build();
    }
}
