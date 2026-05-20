package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;

class DumpUpgrader8to9Test
{
    private static final DumpUpgrader8to9.IconBlobCopier NOOP_COPIER = ( _, blobKey ) -> blobKey;

    @Test
    void readProjectMetadata_returns_repo_id_and_metadata_from_v8_repo_config_node()
    {
        final NodeStoreVersion nodeVersion =
            createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", "Superhero Blog", "Sample blog site" );

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER );

        assertThat( entry ).isNotNull();
        assertThat( entry.getKey() ).isEqualTo( RepositoryId.from( "com.enonic.cms.sample-blog" ) );
        assertThat( entry.getValue().displayName() ).isEqualTo( "Superhero Blog" );
        assertThat( entry.getValue().description() ).isEqualTo( "Sample blog site" );
        assertThat( entry.getValue().icon() ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_for_non_project_repo()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "system-repo", "Foo", "Bar" );

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_data_set_missing()
    {
        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( new PropertyTree() ).build();

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_project_data_set_missing()
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( "data" );
        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( data ).build();

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER ) ).isNull();
    }

    @Test
    void readProjectMetadata_returns_null_when_display_name_description_parents_and_icon_all_missing()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", null, null );

        assertThat( DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER ) ).isNull();
    }

    @Test
    void readProjectMetadata_extracts_parents_when_present()
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet repoData = data.addSet( "data" );
        final PropertySet projectData = repoData.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        projectData.addStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY, java.util.List.of( "default", "shared" ) );

        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( data ).build();

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER );

        assertThat( entry ).isNotNull();
        assertThat( entry.getValue().parents() ).containsExactly( "default", "shared" );
    }

    @Test
    void readProjectMetadata_returns_entry_when_only_display_name_present()
    {
        final NodeStoreVersion nodeVersion = createV8ProjectRepoConfigNode( "com.enonic.cms.sample-blog", "Superhero Blog", null );

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER );

        assertThat( entry ).isNotNull();
        assertThat( entry.getValue().displayName() ).isEqualTo( "Superhero Blog" );
        assertThat( entry.getValue().description() ).isNull();
        assertThat( entry.getValue().icon() ).isNull();
    }

    @Test
    void readProjectMetadata_extracts_icon_when_present()
    {
        final BinaryReference iconRef = BinaryReference.from( "logo.png" );
        final PropertyTree data = new PropertyTree();
        final PropertySet repoData = data.addSet( "data" );
        final PropertySet projectData = repoData.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, "Superhero Blog" );
        final PropertySet iconSet = projectData.addSet( ProjectConstants.PROJECT_ICON_PROPERTY );
        iconSet.addString( "name", "logo.png" );
        iconSet.addString( "label", "Small" );
        iconSet.addBinaryReference( "binary", iconRef );
        iconSet.addString( "mimeType", "image/png" );
        iconSet.addLong( "size", 726L );
        iconSet.addString( "sha512", "deadbeef" );

        final AttachedBinaries binaries = AttachedBinaries.create().add( new AttachedBinary( iconRef, "system-blob-key" ) ).build();
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "com.enonic.cms.sample-blog" ) )
            .data( data )
            .attachedBinaries( binaries )
            .build();

        final DumpUpgrader8to9.IconBlobCopier copier =
            ( repoId, blobKey ) -> "system-blob-key".equals( blobKey ) && "com.enonic.cms.sample-blog".equals( repoId.toString() )
                ? "project-blob-key"
                : null;

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion, copier );

        assertThat( entry ).isNotNull();
        final ProjectContentRootMetadataUpgrader.IconBinary icon = entry.getValue().icon();
        assertThat( icon ).isNotNull();
        assertThat( icon.mimeType() ).isEqualTo( "image/png" );
        assertThat( icon.size() ).isEqualTo( 726L );
        assertThat( icon.blobKey() ).isEqualTo( "project-blob-key" );
    }

    @Test
    void readProjectMetadata_ignores_icon_when_attached_binary_missing()
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet repoData = data.addSet( "data" );
        final PropertySet projectData = repoData.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, "Superhero Blog" );
        final PropertySet iconSet = projectData.addSet( ProjectConstants.PROJECT_ICON_PROPERTY );
        iconSet.addBinaryReference( "binary", BinaryReference.from( "logo.png" ) );

        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "com.enonic.cms.sample-blog" ) ).data( data ).build();

        final Map.Entry<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> entry =
            DumpUpgrader8to9.readProjectMetadata( nodeVersion, NOOP_COPIER );

        assertThat( entry ).isNotNull();
        assertThat( entry.getValue().icon() ).isNull();
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