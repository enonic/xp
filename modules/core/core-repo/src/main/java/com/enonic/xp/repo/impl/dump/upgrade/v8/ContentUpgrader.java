package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssuePropertyNames;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

public class ContentUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentUpgrader.class );

    private static final List<String> CONTENT_TIME_PROPERTIES =
        List.of( ContentPropertyNames.CREATED_TIME, ContentPropertyNames.MODIFIED_TIME,
                 ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_FROM,
                 ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_TO,
                 ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_TIME,
                 ContentPropertyNames.PUBLISH_INFO + "." + ContentPropertyNames.PUBLISH_FIRST );

    private static final List<String> ISSUE_TIME_PROPERTIES =
        List.of( IssuePropertyNames.CREATED_TIME, IssuePropertyNames.MODIFIED_TIME, "schedule.from", "schedule.to" );

    private static final NodeType ISSUE_COMMENT_NODE_COLLECTION = NodeType.from( "issue_comment" );

    private static final List<String> ISSUE_COMMENT_TIME_PROPERTIES = List.of( "createdTime" );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }

        final List<String> timeProperties = getTimeProperties( nodeVersion.nodeType() );
        if ( timeProperties == null )
        {
            return null;
        }

        return truncateTimeProperties( repositoryId, nodeVersion, timeProperties );
    }

    private static List<String> getTimeProperties( final NodeType nodeType )
    {
        if ( ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeType ) )
        {
            return CONTENT_TIME_PROPERTIES;
        }
        else if ( IssueConstants.ISSUE_NODE_COLLECTION.equals( nodeType ) )
        {
            return ISSUE_TIME_PROPERTIES;
        }
        else if ( ISSUE_COMMENT_NODE_COLLECTION.equals( nodeType ) )
        {
            return ISSUE_COMMENT_TIME_PROPERTIES;
        }
        return null;
    }

    private static NodeStoreVersion truncateTimeProperties( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion,
                                                            final List<String> timeProperties )
    {
        final PropertyTree data = nodeVersion.data();
        boolean modified = false;

        for ( String propertyName : timeProperties )
        {
            final Instant value = data.getInstant( propertyName );
            if ( value != null )
            {
                final Instant truncated = value.truncatedTo( ChronoUnit.MILLIS );
                if ( !truncated.equals( value ) )
                {
                    data.setInstant( propertyName, truncated );
                    modified = true;
                }
            }
        }

        if ( modified )
        {
            LOG.info( "Truncated time properties to millis for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );
        }
        return modified ? nodeVersion : null;
    }
}
