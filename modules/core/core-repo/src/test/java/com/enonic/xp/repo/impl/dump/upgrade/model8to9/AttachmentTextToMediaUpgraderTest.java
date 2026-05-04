package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentTextToMediaUpgraderTest
{
    private static final RepositoryId PROJECT_REPO = RepositoryId.from( ProjectConstants.PROJECT_REPO_ID_PREFIX + "default" );

    private final AttachmentTextToMediaUpgrader upgrader = new AttachmentTextToMediaUpgrader();

    @Test
    void textual_media_with_attachment_text_is_migrated()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet.getString( ContentPropertyNames.MEDIA_TEXT ) ).isEqualTo( "extracted text content" );
        final PropertySet attachmentSet = result.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet.getString( "text" ) ).isNull();
    }

    @Test
    void non_media_node_with_text_on_attachment_is_skipped()
    {
        final NodeStoreVersion nodeVersion = nonMediaNodeWithAttachmentText( "some unrelated text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        final PropertySet attachmentSet = nodeVersion.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet.getString( "text" ) ).isEqualTo( "some unrelated text" );
    }

    @Test
    void already_migrated_textual_media_returns_null()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), null, "already migrated text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        final PropertySet mediaSet = nodeVersion.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet.getString( ContentPropertyNames.MEDIA_TEXT ) ).isEqualTo( "already migrated text" );
    }

    @Test
    void node_outside_project_repo_is_skipped()
    {
        final RepositoryId systemRepo = RepositoryId.from( "system-repo" );
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( systemRepo, nodeVersion );

        assertThat( result ).isNull();
        final PropertySet attachmentSet = nodeVersion.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet.getString( "text" ) ).isEqualTo( "extracted text content" );
    }

    @Test
    void non_content_node_is_skipped()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, ContentTypeName.documentMedia().toString() );
        final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, "doc.pdf" );
        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, "doc.pdf" );
        attachmentSet.addString( "text", "extracted text content" );

        final NodeStoreVersion nodeVersion =
            NodeStoreVersion.create().id( NodeId.from( "non-content-node" ) ).nodeType( NodeType.from( "other" ) ).data( data ).build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void non_textual_media_node_is_skipped()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.imageMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        final PropertySet attachmentSet = nodeVersion.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet.getString( "text" ) ).isEqualTo( "extracted text content" );
    }

    @Test
    void textual_media_without_attachment_text_returns_null()
    {
        final NodeStoreVersion nodeVersion = textualMediaNode( ContentTypeName.documentMedia().toString(), null, null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void existing_media_text_is_not_overwritten_but_attachment_text_is_cleared()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "stale attachment text", "newer media text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PropertySet mediaSet = result.data().getSet( ContentPropertyNames.MEDIA );
        assertThat( mediaSet.getString( ContentPropertyNames.MEDIA_TEXT ) ).isEqualTo( "newer media text" );
        final PropertySet attachmentSet = result.data().getSet( ContentPropertyNames.ATTACHMENT );
        assertThat( attachmentSet.getString( "text" ) ).isNull();
    }

    private static NodeStoreVersion textualMediaNode( final String type, final String attachmentText, final String mediaText )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, type );

        final PropertySet mediaSet = data.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, "doc.txt" );
        if ( mediaText != null )
        {
            mediaSet.addString( ContentPropertyNames.MEDIA_TEXT, mediaText );
        }

        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, "doc.txt" );
        if ( attachmentText != null )
        {
            attachmentSet.addString( "text", attachmentText );
        }

        return NodeStoreVersion.create()
            .id( NodeId.from( "textual-media-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();
    }

    private static NodeStoreVersion nonMediaNodeWithAttachmentText( final String attachmentText )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "myapp:custom" );

        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, "file.bin" );
        attachmentSet.addString( "text", attachmentText );

        return NodeStoreVersion.create()
            .id( NodeId.from( "non-media-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();
    }
}
