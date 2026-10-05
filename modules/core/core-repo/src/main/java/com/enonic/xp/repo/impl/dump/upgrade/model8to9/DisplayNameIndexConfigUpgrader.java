package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

/**
 * Adds the language-aware {@code displayName} index config that XP 8 writes for content with a language.
 * <p>
 * Ordering children by {@code displayName} uses the collation of the parent's language, which reads a language-specific
 * order-by field. That field is only indexed when the {@code displayName} index config carries the language.
 * Without it, migrated content sorts in arbitrary order. Reindexing does not help, because the index config is part of the
 * node version.
 */
public class DisplayNameIndexConfigUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( DisplayNameIndexConfigUpgrader.class );

    private static final IndexPath DISPLAY_NAME_PATH = IndexPath.from( ContentPropertyNames.DISPLAY_NAME );

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

        if ( !( nodeVersion.indexConfigDocument() instanceof PatternIndexConfigDocument indexConfigDocument ) )
        {
            return null;
        }

        final String languageTag = nodeVersion.data().getString( ContentPropertyNames.LANGUAGE );
        if ( languageTag == null || languageTag.isBlank() )
        {
            return null;
        }

        final Locale language = Locale.forLanguageTag( languageTag );

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( indexConfigDocument );
        builder.analyzer( indexConfigDocument.getAnalyzer() );

        for ( final PathIndexConfig pathIndexConfig : indexConfigDocument.getPathIndexConfigs() )
        {
            if ( DISPLAY_NAME_PATH.equals( pathIndexConfig.getIndexPath() ) )
            {
                final IndexConfig indexConfig = pathIndexConfig.getIndexConfig();
                if ( indexConfig.isEnabled() && indexConfig.getLanguages().contains( language ) )
                {
                    return null;
                }
                builder.remove( pathIndexConfig );
            }
        }

        builder.add( DISPLAY_NAME_PATH, IndexConfig.create( IndexConfig.FULLTEXT ).addLanguage( language ).build() );

        LOG.info( "Added [{}] index config with language [{}] for node [{}] in repository [{}]", ContentPropertyNames.DISPLAY_NAME,
                  language.toLanguageTag(), nodeVersion.id(), repositoryId );

        return NodeStoreVersion.create( nodeVersion ).indexConfigDocument( builder.build() ).build();
    }
}
