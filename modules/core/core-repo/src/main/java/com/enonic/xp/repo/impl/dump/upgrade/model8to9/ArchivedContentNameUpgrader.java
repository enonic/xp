package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.text.Normalizer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

/**
 * Normalizes the original name and parent path of archived content to Unicode NFC, the same way
 * {@link NodePathNormalizeUpgrader} does for node paths, so archived content can still be restored.
 */
public class ArchivedContentNameUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( ArchivedContentNameUpgrader.class );

    private static final List<String> NAME_PROPERTIES = List.of( ContentPropertyNames.ORIGINAL_NAME, ContentPropertyNames.ORIGINAL_PARENT_PATH );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) ||
            !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        boolean modified = false;

        for ( String propertyName : NAME_PROPERTIES )
        {
            final String value = data.getString( propertyName );
            if ( value != null )
            {
                final String normalized = Normalizer.normalize( value, Normalizer.Form.NFC );
                if ( !normalized.equals( value ) )
                {
                    LOG.warn( "Normalizing [{}] [{}] to Unicode NFC [{}] for node [{}] in repository [{}]", propertyName, value, normalized,
                              nodeVersion.id(), repositoryId );
                    data.setString( propertyName, normalized );
                    modified = true;
                }
            }
        }

        return modified ? nodeVersion : null;
    }
}
