package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageTagUpgraderTest
{
    private final LanguageTagUpgrader upgrader = new LanguageTagUpgrader();

    private static final RepositoryId DEFAULT_REPO = RepositoryId.from( "com.enonic.cms.default" );

    @Test
    void upgrades_underscore_to_hyphen()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "en_US" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( ContentPropertyNames.LANGUAGE ) ).isEqualTo( "en-US" );
    }

    @Test
    void upgrades_language_with_script()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "zh_Hans_CN" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( ContentPropertyNames.LANGUAGE ) ).isEqualTo( "zh-Hans-CN" );
    }

    @Test
    void skips_already_correct_language_tag()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "en-US" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_simple_language_code()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "en" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_null_language()
    {
        final PropertyTree data = new PropertyTree();
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_non_content_repo()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion( "en_US" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( RepositoryId.from( "system.repo" ), nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_non_content_node_type()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.LANGUAGE, "en_US" );

        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "test-node" ) ).nodeType( NodeType.from( "issue" ) ).data( data ).build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    private static NodeStoreVersion createNodeVersion( final String language )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.LANGUAGE, language );

        return NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();
    }
}
