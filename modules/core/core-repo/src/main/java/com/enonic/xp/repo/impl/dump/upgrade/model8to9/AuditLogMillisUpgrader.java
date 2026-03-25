package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

public class AuditLogMillisUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AuditLogMillisUpgrader.class );

    private static final RepositoryId AUDIT_LOG_REPO_ID = RepositoryId.from( "system.auditlog" );

    private static final NodeType AUDIT_LOG_NODE_TYPE = NodeType.from( "auditlog" );

    private static final String TIME_PROPERTY = "time";

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !AUDIT_LOG_REPO_ID.equals( repositoryId ) || !AUDIT_LOG_NODE_TYPE.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }
        final PropertyTree data = nodeVersion.data();

        final Instant value = data.getInstant( TIME_PROPERTY );
        if ( value != null )
        {
            final Instant truncated = value.truncatedTo( ChronoUnit.MILLIS );
            if ( !truncated.equals( value ) )
            {
                data.setInstant( TIME_PROPERTY, truncated );
                LOG.info( "Truncated time property to millis for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
                return nodeVersion;
            }
        }

        return null;
    }
}
