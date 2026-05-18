package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;


/**
 * For a project repo {@code /content} node, populates its {@code displayName} and project
 * {@code description} from the project's repository config node in the system repo. The source
 * data is collected up-front by {@link DumpUpgrader8to9} during a pre-pass over the system repo.
 */
public class ProjectContentRootMetadataUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectContentRootMetadataUpgrader.class );

    private final Map<RepositoryId, ProjectMetadata> metadata;

    public ProjectContentRootMetadataUpgrader( final Map<RepositoryId, ProjectMetadata> metadata )
    {
        this.metadata = metadata;
    }

    public NodeStoreVersion upgrade( final RepositoryId repositoryId, final NodePath nodePath, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }
        if ( nodePath == null || !ContentConstants.CONTENT_ROOT_PATH.equals( nodePath ) )
        {
            return null;
        }

        final ProjectMetadata pm = metadata.get( repositoryId );
        if ( pm == null )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        boolean modified = false;

        if ( pm.displayName() != null )
        {
            data.setString( ContentPropertyNames.DISPLAY_NAME, pm.displayName() );
            modified = true;
        }

        if ( pm.description() != null )
        {
            PropertySet contentData = data.getSet( ContentPropertyNames.DATA );
            if ( contentData == null )
            {
                contentData = data.addSet( ContentPropertyNames.DATA );
            }
            contentData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, pm.description() );
            modified = true;
        }

        if ( modified )
        {
            LOG.info( "Populated /content node displayName/description for project repo [{}]", repositoryId );
        }
        return modified ? nodeVersion : null;
    }

    public record ProjectMetadata(String displayName, String description)
    {
    }
}
