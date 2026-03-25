package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repository.RepositoryId;

public class IndexConfigLanguageUpgrader
    implements NodeVersionUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( IndexConfigLanguageUpgrader.class );

    @Override
    public NodeStoreVersion upgradeNodeVersion( final RepositoryId repositoryId, final NodeStoreVersion nodeVersion )
    {
        final IndexConfigDocument indexConfigDocument = nodeVersion.indexConfigDocument();
        if ( !( indexConfigDocument instanceof PatternIndexConfigDocument patternConfig ) )
        {
            return null;
        }

        boolean changed = false;

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();
        builder.analyzer( patternConfig.getAnalyzer() );

        final IndexConfig upgradedDefault = upgradeIndexConfig( patternConfig.getDefaultConfig() );
        if ( upgradedDefault != null )
        {
            changed = true;
            builder.defaultConfig( upgradedDefault );
        }
        else
        {
            builder.defaultConfig( patternConfig.getDefaultConfig() );
        }

        for ( final PathIndexConfig pathIndexConfig : patternConfig.getPathIndexConfigs() )
        {
            final IndexConfig upgradedConfig = upgradeIndexConfig( pathIndexConfig.getIndexConfig() );
            if ( upgradedConfig != null )
            {
                changed = true;
                builder.add( PathIndexConfig.create().path( pathIndexConfig.getIndexPath() ).indexConfig( upgradedConfig ).build() );
            }
            else
            {
                builder.add( pathIndexConfig );
            }
        }

        final AllTextIndexConfig upgradedAllText = upgradeAllTextConfig( patternConfig.getAllTextConfig() );
        if ( upgradedAllText != null )
        {
            changed = true;
            builder.allTextConfig( upgradedAllText );
        }
        else
        {
            builder.allTextConfig( patternConfig.getAllTextConfig() );
        }

        if ( !changed )
        {
            return null;
        }

        LOG.info( "Upgraded index config languages for node [{}] in repository [{}]", nodeVersion.id(), repositoryId );

        return NodeStoreVersion.create( nodeVersion ).indexConfigDocument( builder.build() ).build();
    }

    private static IndexConfig upgradeIndexConfig( final IndexConfig config )
    {
        if ( config.getLanguages().isEmpty() )
        {
            return null;
        }

        final List<Locale> upgraded = upgradeLanguages( config.getLanguages() );
        if ( upgraded == null )
        {
            return null;
        }

        final IndexConfig.Builder builder = IndexConfig.create()
            .decideByType( config.isDecideByType() )
            .enabled( config.isEnabled() )
            .nGram( config.isnGram() )
            .fulltext( config.isFulltext() )
            .includeInAllText( config.isIncludeInAllText() )
            .path( config.isPath() );
        config.getIndexValueProcessors().forEach( builder::addIndexValueProcessor );
        upgraded.forEach( builder::addLanguage );
        return builder.build();
    }

    private static AllTextIndexConfig upgradeAllTextConfig( final AllTextIndexConfig config )
    {
        if ( config.getLanguages().isEmpty() )
        {
            return null;
        }

        final List<Locale> upgraded = upgradeLanguages( config.getLanguages() );
        if ( upgraded == null )
        {
            return null;
        }

        final AllTextIndexConfig.Builder builder =
            AllTextIndexConfig.create().enabled( config.isEnabled() ).nGram( config.isnGram() ).fulltext( config.isFulltext() );
        upgraded.forEach( builder::addLanguage );
        return builder.build();
    }

    private static List<Locale> upgradeLanguages( final List<Locale> languages )
    {
        boolean changed = false;
        final List<Locale> result = new ArrayList<>( languages.size() );

        for ( final Locale language : languages )
        {
            final Locale normalized = normalizeLanguage( language );
            if ( !normalized.equals( language ) )
            {
                changed = true;
            }
            result.add( normalized );
        }

        return changed ? result : null;
    }

    private static Locale normalizeLanguage( final Locale language )
    {
        final String tag = language.toLanguageTag();
        final String normalized = Locale.forLanguageTag( tag ).toLanguageTag();
        if ( "no".equals( normalized ) )
        {
            return Locale.forLanguageTag( "nb" );
        }
        return Locale.forLanguageTag( normalized );
    }
}
