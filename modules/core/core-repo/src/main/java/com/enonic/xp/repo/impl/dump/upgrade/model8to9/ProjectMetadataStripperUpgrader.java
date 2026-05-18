package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;


/**
 * Removes {@code displayName} and {@code description} from the project's repository config node in the system repo.
 * Those fields now live on the project's {@code /content} node (see {@link ProjectContentRootMetadataUpgrader}).
 */
public class ProjectMetadataStripperUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectMetadataStripperUpgrader.class );

    private static final String REPO_DATA_PROPERTY = "data";

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        if ( !data.hasProperty( REPO_DATA_PROPERTY ) )
        {
            return null;
        }
        final PropertySet repoData = data.getSet( REPO_DATA_PROPERTY );
        if ( repoData == null )
        {
            return null;
        }
        final PropertySet projectData = repoData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        if ( projectData == null )
        {
            return null;
        }

        boolean modified = false;
        if ( projectData.hasProperty( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY ) )
        {
            projectData.removeProperties( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY );
            modified = true;
        }
        if ( projectData.hasProperty( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) )
        {
            projectData.removeProperties( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY );
            modified = true;
        }

        if ( modified )
        {
            LOG.info( "Stripped legacy displayName/description from system repo project config node [{}]", nodeVersion.id() );
        }
        return modified ? nodeVersion : null;
    }
}
