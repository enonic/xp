package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

public class LanguageTagUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( LanguageTagUpgrader.class );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        if ( !repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return null;
        }

        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return null;
        }

        final PropertyTree data = nodeVersion.data();
        final String language = data.getString( ContentPropertyNames.LANGUAGE );
        if ( language == null || !language.contains( "_" ) )
        {
            return null;
        }

        final String languageTag = Locale.forLanguageTag( language.replace( '_', '-' ) ).toLanguageTag();
        data.setString( ContentPropertyNames.LANGUAGE, languageTag );

        LOG.info( "Upgraded language [{}] to [{}] for node [{}] in repository [{}]", language, languageTag, nodeVersion.id(),
                  repositoryId );

        return nodeVersion;
    }
}
