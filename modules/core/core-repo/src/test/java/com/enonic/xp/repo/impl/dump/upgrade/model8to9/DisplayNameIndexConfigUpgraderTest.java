package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;

class DisplayNameIndexConfigUpgraderTest
{
    private static final RepositoryId DEFAULT_REPO = RepositoryId.from( "com.enonic.cms.default" );

    private static final IndexPath DISPLAY_NAME = IndexPath.from( ContentPropertyNames.DISPLAY_NAME );

    private final DisplayNameIndexConfigUpgrader upgrader = new DisplayNameIndexConfigUpgrader();

    @Test
    void adds_display_name_config_with_language()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .analyzer( "document_index_default" )
            .defaultConfig( IndexConfig.BY_TYPE )
            .add( ContentPropertyNames.LANGUAGE, IndexConfig.NGRAM )
            .build();

        final NodeStoreVersion result =
            upgrader.upgradeNodeVersion( DEFAULT_REPO, createNodeVersion( ContentConstants.CONTENT_NODE_COLLECTION, "en-GB", indexConfig ) );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument upgraded = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( upgraded.getConfigForPath( DISPLAY_NAME ) ).isEqualTo(
            IndexConfig.create( IndexConfig.FULLTEXT ).addLanguage( Locale.forLanguageTag( "en-GB" ) ).build() );
        assertThat( upgraded.getConfigForPath( IndexPath.from( ContentPropertyNames.LANGUAGE ) ) ).isEqualTo( IndexConfig.NGRAM );
        assertThat( upgraded.getAnalyzer() ).isEqualTo( "document_index_default" );
    }

    @Test
    void replaces_display_name_config_without_language()
    {
        final PatternIndexConfigDocument indexConfig =
            PatternIndexConfigDocument.create().add( ContentPropertyNames.DISPLAY_NAME, IndexConfig.MINIMAL ).build();

        final NodeStoreVersion result =
            upgrader.upgradeNodeVersion( DEFAULT_REPO, createNodeVersion( ContentConstants.CONTENT_NODE_COLLECTION, "no", indexConfig ) );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument upgraded = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( upgraded.getPathIndexConfigs() ).filteredOn( c -> c.getIndexPath().equals( DISPLAY_NAME ) ).hasSize( 1 );
        assertThat( upgraded.getConfigForPath( DISPLAY_NAME ).getLanguages() ).containsExactly( Locale.forLanguageTag( "no" ) );
    }

    @Test
    void skips_when_display_name_config_has_language()
    {
        final PatternIndexConfigDocument indexConfig = PatternIndexConfigDocument.create()
            .add( ContentPropertyNames.DISPLAY_NAME, IndexConfig.create( IndexConfig.FULLTEXT ).addLanguage( Locale.ENGLISH ).build() )
            .build();

        assertThat( upgrader.upgradeNodeVersion( DEFAULT_REPO, createNodeVersion( ContentConstants.CONTENT_NODE_COLLECTION, "en",
                                                                                   indexConfig ) ) ).isNull();
    }

    @Test
    void skips_content_without_language()
    {
        assertThat( upgrader.upgradeNodeVersion( DEFAULT_REPO, createNodeVersion( ContentConstants.CONTENT_NODE_COLLECTION, null,
                                                                                   PatternIndexConfigDocument.empty() ) ) ).isNull();
    }

    @Test
    void skips_non_content_node()
    {
        assertThat( upgrader.upgradeNodeVersion( DEFAULT_REPO, createNodeVersion( NodeType.DEFAULT_NODE_COLLECTION, "en",
                                                                                   PatternIndexConfigDocument.empty() ) ) ).isNull();
    }

    @Test
    void skips_non_project_repo()
    {
        assertThat( upgrader.upgradeNodeVersion( RepositoryId.from( "system-repo" ),
                                                 createNodeVersion( ContentConstants.CONTENT_NODE_COLLECTION, "en",
                                                                    PatternIndexConfigDocument.empty() ) ) ).isNull();
    }

    private static NodeStoreVersion createNodeVersion( final NodeType nodeType, final String language,
                                                       final PatternIndexConfigDocument indexConfig )
    {
        final PropertyTree data = new PropertyTree();
        if ( language != null )
        {
            data.setString( ContentPropertyNames.LANGUAGE, language );
        }
        return NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( nodeType )
            .data( data )
            .indexConfigDocument( indexConfig )
            .build();
    }
}
