package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;

class DumpUpgrader8to9Test
{
    @Test
    void readProjectMetadata_returns_repo_id_and_metadata_from_v8_repo_config_node()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", "Superhero Blog", "Sample blog site" );

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion );

        assertThat( entry ).isNotNull();
        assertThat( entry.getKey() ).isEqualTo( RepositoryId.from( "com.enonic.cms.sample-blog" ) );
        assertThat( entry.getValue().displayName() ).isEqualTo( "Superhero Blog" );
        assertThat( entry.getValue().description() ).isEqualTo( "Sample blog site" );
    }

    @Test
    void readProjectMetadata_returns_null_for_non_project_repo()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "system-repo", "Foo", "Bar" );

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_data_set_missing()
    {
        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( new PropertyTree() ).build();

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_project_data_set_missing()
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( "data" );
        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( data ).build();

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_display_name_and_description_both_missing()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", null, null );

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_entry_when_only_display_name_present()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", "Superhero Blog", null );

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion );

        assertThat( entry ).isNotNull();
        assertThat( entry.getValue().displayName() ).isEqualTo( "Superhero Blog" );
        assertThat( entry.getValue().description() ).isNull();
    }

    private static NodeStoreVersion createV8ProjectRepoConfigNode( final String repoId, final String displayName, final String description )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet repoData = data.addSet( "data" );
        final PropertySet projectData = repoData.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        if ( displayName != null )
        {
            projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, displayName );
        }
        if ( description != null )
        {
            projectData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, description );
        }
        return NodeStoreVersion.create().id( NodeId.from( repoId ) ).data( data ).build();
    }
}
