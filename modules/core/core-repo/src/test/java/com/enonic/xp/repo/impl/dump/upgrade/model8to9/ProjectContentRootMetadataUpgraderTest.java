package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectContentRootMetadataUpgraderTest
{
    private static final RepositoryId PROJECT_REPO = RepositoryId.from( "com.enonic.cms.sample-blog" );

    @Test
    void applies_display_name_and_description()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", "Sample blog site" ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Superhero Blog" );
        assertThat( result.data().getSet( ContentPropertyNames.DATA ).getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).isEqualTo(
            "Sample blog site" );
    }

    @Test
    void applies_only_display_name_when_description_missing()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", null ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Superhero Blog" );
        assertThat( result.data().getSet( ContentPropertyNames.DATA ).hasProperty( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).isFalse();
    }

    @Test
    void applies_parents_to_content_data_set()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader( Map.of( PROJECT_REPO,
                                                                                                            new ProjectContentRootMetadataUpgrader.ProjectMetadata(
                                                                                                                null, null,
                                                                                                                java.util.List.of( "default",
                                                                                                                                   "shared" ),
                                                                                                                null ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getSet( ContentPropertyNames.DATA ).getStrings( ProjectConstants.PROJECT_PARENTS_PROPERTY ) ).containsExactly(
            "default", "shared" );
    }

    @Test
    void applies_only_description_when_display_name_missing()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( null, "Sample blog site" ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Content" );
        assertThat( result.data().getSet( ContentPropertyNames.DATA ).getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).isEqualTo(
            "Sample blog site" );
    }

    @Test
    void creates_data_set_when_missing()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( null, "Sample blog site" ) ) );

        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create().id( NodeId.from( "content-root" ) ).data( data ).build();

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        assertThat( result.data().getSet( ContentPropertyNames.DATA ).getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).isEqualTo(
            "Sample blog site" );
    }

    @Test
    void skips_non_project_repo()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", "Sample blog site" ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result =
            upgrader.upgrade( RepositoryId.from( "system-repo" ), ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Content" );
    }

    @Test
    void skips_non_content_root_path()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", "Sample blog site" ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, new NodePath( "/content/child" ), nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Content" );
    }

    @Test
    void skips_null_path()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", "Sample blog site" ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, null, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Content" );
    }

    @Test
    void skips_when_repo_metadata_missing()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader( Map.of() );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( ContentPropertyNames.DISPLAY_NAME ) ).isEqualTo( "Content" );
    }

    @Test
    void preserves_existing_data_properties()
    {
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( "Superhero Blog", "Sample blog site" ) ) );

        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.DISPLAY_NAME, "Content" );
        final PropertySet existingData = data.addSet( ContentPropertyNames.DATA );
        existingData.setString( "existingKey", "existingValue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create().id( NodeId.from( "content-root" ) ).data( data ).build();

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isSameAs( nodeVersion );
        final PropertySet contentData = result.data().getSet( ContentPropertyNames.DATA );
        assertThat( contentData.getString( "existingKey" ) ).isEqualTo( "existingValue" );
        assertThat( contentData.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).isEqualTo( "Sample blog site" );
    }

    @Test
    void applies_icon_as_thumbnail_attachment_with_attached_binary()
    {
        final ProjectContentRootMetadataUpgrader.IconBinary icon =
            new ProjectContentRootMetadataUpgrader.IconBinary( "image/png", 726L, "project-blob-key" );
        final ProjectContentRootMetadataUpgrader upgrader = new ProjectContentRootMetadataUpgrader(
            Map.of( PROJECT_REPO, new ProjectContentRootMetadataUpgrader.ProjectMetadata( null, null, java.util.List.of(), icon ) ) );

        final NodeStoreVersion nodeVersion = createContentRootVersion( "Content" );

        final NodeStoreVersion result = upgrader.upgrade( PROJECT_REPO, ContentConstants.CONTENT_ROOT_PATH, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet attachmentSet = result.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet ).isNotNull();
        assertThat( attachmentSet.getString( ContentPropertyNames.ATTACHMENT_NAME ) ).isEqualTo( AttachmentNames.THUMBNAIL );
        assertThat( attachmentSet.hasProperty( ContentPropertyNames.ATTACHMENT_LABEL ) ).isFalse();
        assertThat( attachmentSet.getBinaryReference( ContentPropertyNames.ATTACHMENT_BINARY_REF ) ).isEqualTo(
            BinaryReference.from( AttachmentNames.THUMBNAIL ) );
        assertThat( attachmentSet.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) ).isEqualTo( "image/png" );
        assertThat( attachmentSet.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) ).isEqualTo( 726L );
        assertThat( attachmentSet.hasProperty( ContentPropertyNames.ATTACHMENT_SHA512 ) ).isFalse();

        final AttachedBinary attachedBinary = result.attachedBinaries().getByBinaryReference( BinaryReference.from( AttachmentNames.THUMBNAIL ) );
        assertThat( attachedBinary ).isNotNull();
        assertThat( attachedBinary.getBlobKey() ).isEqualTo( "project-blob-key" );
    }

    private static NodeStoreVersion createContentRootVersion( final String existingDisplayName )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.DISPLAY_NAME, existingDisplayName );
        data.addSet( ContentPropertyNames.DATA );
        return NodeStoreVersion.create().id( NodeId.from( "content-root" ) ).data( data ).build();
    }
}
