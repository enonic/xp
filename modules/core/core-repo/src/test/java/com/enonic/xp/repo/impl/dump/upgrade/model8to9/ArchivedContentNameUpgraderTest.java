package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivedContentNameUpgraderTest
{
    private static final RepositoryId PROJECT_REPO = RepositoryId.from( "com.enonic.cms.default" );

    private final ArchivedContentNameUpgrader upgrader = new ArchivedContentNameUpgrader();

    @Test
    void decomposed_original_name_and_parent_path_are_normalized_to_nfc()
    {
        final NodeStoreVersion nodeVersion =
            createVersion( ContentConstants.CONTENT_NODE_COLLECTION, "gård.jpg", "/content/blåbær" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( ContentPropertyNames.ORIGINAL_NAME ) ).isEqualTo( "gård.jpg" );
        assertThat( result.data().getString( ContentPropertyNames.ORIGINAL_PARENT_PATH ) ).isEqualTo( "/content/blåbær" );
    }

    @Test
    void original_name_with_whitespace_is_trimmed()
    {
        final NodeStoreVersion nodeVersion = createVersion( ContentConstants.CONTENT_NODE_COLLECTION, "ga\u030Ard ", " /content" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( ContentPropertyNames.ORIGINAL_NAME ) ).isEqualTo( "g\u00E5rd" );
        assertThat( result.data().getString( ContentPropertyNames.ORIGINAL_PARENT_PATH ) ).isEqualTo( "/content" );
    }

    @Test
    void composed_names_are_unchanged()
    {
        final NodeStoreVersion nodeVersion = createVersion( ContentConstants.CONTENT_NODE_COLLECTION, "gård.jpg", "/content" );

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
    }

    @Test
    void not_archived_content_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = createVersion( ContentConstants.CONTENT_NODE_COLLECTION, null, null );

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
    }

    @Test
    void non_content_node_is_skipped()
    {
        final NodeStoreVersion nodeVersion = createVersion( NodeType.from( "other" ), "gård", null );

        assertThat( upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion ) ).isNull();
        assertThat( nodeVersion.data().getString( ContentPropertyNames.ORIGINAL_NAME ) ).isEqualTo( "gård" );
    }

    @Test
    void non_project_repository_is_skipped()
    {
        final NodeStoreVersion nodeVersion = createVersion( ContentConstants.CONTENT_NODE_COLLECTION, "gård", null );

        assertThat( upgrader.upgradeNodeVersion( SystemConstants.SYSTEM_REPO_ID, nodeVersion ) ).isNull();
    }

    private static NodeStoreVersion createVersion( final NodeType nodeType, final String originalName, final String originalParentPath )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.DISPLAY_NAME, "Gård" );
        if ( originalName != null )
        {
            data.setString( ContentPropertyNames.ORIGINAL_NAME, originalName );
        }
        if ( originalParentPath != null )
        {
            data.setString( ContentPropertyNames.ORIGINAL_PARENT_PATH, originalParentPath );
        }
        return NodeStoreVersion.create().id( NodeId.from( "archived" ) ).nodeType( nodeType ).data( data ).build();
    }
}
