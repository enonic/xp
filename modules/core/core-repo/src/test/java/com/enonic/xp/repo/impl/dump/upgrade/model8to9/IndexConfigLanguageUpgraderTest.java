package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;

class IndexConfigLanguageUpgraderTest
{
    private final IndexConfigLanguageUpgrader upgrader = new IndexConfigLanguageUpgrader();

    private static final RepositoryId REPO = RepositoryId.from( "com.enonic.cms.default" );

    @Test
    void upgrades_no_to_nb_in_path_config()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .defaultConfig( IndexConfig.MINIMAL )
            .add( PathIndexConfig.create()
                      .path( IndexPath.from( "data" ) )
                      .indexConfig( IndexConfig.create()
                                        .enabled( true )
                                        .fulltext( true )
                                        .nGram( true )
                                        .decideByType( false )
                                        .includeInAllText( true )
                                        .path( false )
                                        .addLanguage( Locale.forLanguageTag( "no" ) )
                                        .build() )
                      .build() )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, createNodeVersion( indexConfig ) );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument upgraded = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( upgraded.getConfigForPath( IndexPath.from( "data" ) ).getLanguages() ).containsExactly( Locale.forLanguageTag( "nb" ) );
    }

    @Test
    void upgrades_no_to_nb_in_default_config()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .defaultConfig( IndexConfig.create()
                                .enabled( true )
                                .fulltext( false )
                                .nGram( false )
                                .decideByType( false )
                                .includeInAllText( false )
                                .path( false )
                                .addLanguage( Locale.forLanguageTag( "no" ) )
                                .build() )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, createNodeVersion( indexConfig ) );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument upgraded = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( upgraded.getDefaultConfig().getLanguages() ).containsExactly( Locale.forLanguageTag( "nb" ) );
    }

    @Test
    void skips_when_no_languages()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.MINIMAL ).build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, createNodeVersion( indexConfig ) );

        assertThat( result ).isNull();
    }

    @Test
    void skips_when_already_correct()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .defaultConfig( IndexConfig.create()
                                .enabled( true )
                                .fulltext( false )
                                .nGram( false )
                                .decideByType( false )
                                .includeInAllText( false )
                                .path( false )
                                .addLanguage( Locale.forLanguageTag( "en" ) )
                                .build() )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, createNodeVersion( indexConfig ) );

        assertThat( result ).isNull();
    }

    @Test
    void preserves_valid_languages()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .defaultConfig( IndexConfig.create()
                                .enabled( true )
                                .fulltext( true )
                                .nGram( true )
                                .decideByType( false )
                                .includeInAllText( true )
                                .path( false )
                                .addLanguage( Locale.forLanguageTag( "no" ) )
                                .addLanguage( Locale.forLanguageTag( "en" ) )
                                .build() )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( REPO, createNodeVersion( indexConfig ) );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument upgraded = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( upgraded.getDefaultConfig().getLanguages() ).containsExactly( Locale.forLanguageTag( "nb" ),
                                                                                  Locale.forLanguageTag( "en" ) );
    }

    private static NodeStoreVersion createNodeVersion( final PatternIndexConfigDocument indexConfig )
    {
        return NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .indexConfigDocument( indexConfig )
            .build();
    }
}
