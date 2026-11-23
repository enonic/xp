package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
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

    private static final IndexPath LEGACY_TEXT_PATH = IndexPath.from( "attachment.text" );

    private static final IndexPath MEDIA_TEXT_PATH = IndexPath.from( "data.media.text" );

    private static final IndexConfig LEGACY_TEXT_CONFIG =
        IndexConfig.create().enabled( true ).fulltext( true ).includeInAllText( true ).nGram( true ).decideByType( false ).build();

    private final AttachmentTextToMediaUpgrader upgrader = new AttachmentTextToMediaUpgrader();

    @Test
    void textual_media_with_attachment_text_is_migrated()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( "data.media.text" ) ).isEqualTo( "extracted text content" );
        assertThat( result.data().getString( "data.media.attachment" ) ).isEqualTo( "doc.pdf" );
        assertThat( result.data().getSet( ContentPropertyNames.MEDIA ) ).isNull();
        assertThat( result.data().getSet( ContentPropertyNames.ATTACHMENT ).getString( "text" ) ).isNull();
    }

    @Test
    void index_config_of_attachment_text_is_replaced_with_media_text()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        final PatternIndexConfigDocument indexConfig = (PatternIndexConfigDocument) result.indexConfigDocument();
        assertThat( indexConfig.getPathIndexConfigs() ).noneMatch( c -> LEGACY_TEXT_PATH.equals( c.getIndexPath() ) );
        final IndexConfig mediaTextConfig = indexConfig.getConfigForPath( MEDIA_TEXT_PATH );
        assertThat( mediaTextConfig.isFulltext() ).isTrue();
        assertThat( mediaTextConfig.isIncludeInAllText() ).isTrue();
        assertThat( mediaTextConfig.isnGram() ).isTrue();
        assertThat( indexConfig.getAnalyzer() ).isEqualTo( "document_index_default" );
        assertThat( indexConfig.getConfigForPath( IndexPath.from( "data" ) ) ).isEqualTo( IndexConfig.BY_TYPE );
    }

    @Test
    void legacy_string_media_is_converted_to_set_with_text()
    {
        final PropertyTree data = textualMediaData( ContentTypeName.textMedia().toString(), "plain text" );
        final PropertySet contentData = data.getSet( ContentPropertyNames.DATA );
        contentData.removeProperties( ContentPropertyNames.MEDIA );
        contentData.setString( ContentPropertyNames.MEDIA, "doc.pdf" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, contentNode( data ) );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( "data.media.attachment" ) ).isEqualTo( "doc.pdf" );
        assertThat( result.data().getString( "data.media.text" ) ).isEqualTo( "plain text" );
    }

    @Test
    void missing_media_is_created_from_text_attachment()
    {
        final PropertyTree data = textualMediaData( ContentTypeName.textMedia().toString(), "plain text" );
        data.getSet( ContentPropertyNames.DATA ).removeProperties( ContentPropertyNames.MEDIA );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, contentNode( data ) );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( "data.media.attachment" ) ).isEqualTo( "doc.pdf" );
        assertThat( result.data().getString( "data.media.text" ) ).isEqualTo( "plain text" );
    }

    @Test
    void non_media_node_with_text_on_attachment_is_skipped()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, "myapp:custom" );
        data.addSet( ContentPropertyNames.DATA );
        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, "file.bin" );
        attachmentSet.addString( "text", "some unrelated text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, contentNode( data ) );

        assertThat( result ).isNull();
        assertThat( attachmentSet.getString( "text" ) ).isEqualTo( "some unrelated text" );
    }

    @Test
    void existing_media_text_is_not_overwritten_but_attachment_text_is_cleared()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "stale attachment text", "newer media text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.data().getString( "data.media.text" ) ).isEqualTo( "newer media text" );
        assertThat( result.data().getSet( ContentPropertyNames.ATTACHMENT ).getString( "text" ) ).isNull();
    }

    @Test
    void textual_media_without_attachment_text_returns_null()
    {
        final NodeStoreVersion nodeVersion = textualMediaNode( ContentTypeName.documentMedia().toString(), null, "already migrated text" );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getString( "data.media.text" ) ).isEqualTo( "already migrated text" );
    }

    @Test
    void node_outside_project_repo_is_skipped()
    {
        final NodeStoreVersion nodeVersion =
            textualMediaNode( ContentTypeName.documentMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( RepositoryId.from( "system-repo" ), nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getSet( ContentPropertyNames.ATTACHMENT ).getString( "text" ) ).isEqualTo(
            "extracted text content" );
    }

    @Test
    void non_content_node_is_skipped()
    {
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "non-content-node" ) )
            .nodeType( NodeType.from( "other" ) )
            .data( textualMediaData( ContentTypeName.documentMedia().toString(), "extracted text content" ) )
            .indexConfigDocument( legacyIndexConfig() )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void non_textual_media_node_is_skipped()
    {
        final NodeStoreVersion nodeVersion = textualMediaNode( ContentTypeName.imageMedia().toString(), "extracted text content", null );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( PROJECT_REPO, nodeVersion );

        assertThat( result ).isNull();
        assertThat( nodeVersion.data().getSet( ContentPropertyNames.ATTACHMENT ).getString( "text" ) ).isEqualTo(
            "extracted text content" );
    }

    private static NodeStoreVersion textualMediaNode( final String type, final String attachmentText, final String mediaText )
    {
        final PropertyTree data = textualMediaData( type, attachmentText );
        if ( mediaText != null )
        {
            data.getSet( ContentPropertyNames.DATA ).getSet( ContentPropertyNames.MEDIA ).addString( ContentPropertyNames.MEDIA_TEXT, mediaText );
        }
        return contentNode( data );
    }

    private static PropertyTree textualMediaData( final String type, final String attachmentText )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ContentPropertyNames.TYPE, type );

        final PropertySet contentData = data.addSet( ContentPropertyNames.DATA );
        final PropertySet mediaSet = contentData.addSet( ContentPropertyNames.MEDIA );
        mediaSet.addString( ContentPropertyNames.MEDIA_ATTACHMENT, "doc.pdf" );

        final PropertySet attachmentSet = data.addSet( ContentPropertyNames.ATTACHMENT );
        attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, "doc.pdf" );
        if ( attachmentText != null )
        {
            attachmentSet.addString( "text", attachmentText );
        }
        return data;
    }

    private static NodeStoreVersion contentNode( final PropertyTree data )
    {
        return NodeStoreVersion.create()
            .id( NodeId.from( "textual-media-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .indexConfigDocument( legacyIndexConfig() )
            .build();
    }

    private static PatternIndexConfigDocument legacyIndexConfig()
    {
        return PatternIndexConfigDocument.create()
            .analyzer( "document_index_default" )
            .add( "data", IndexConfig.BY_TYPE )
            .add( LEGACY_TEXT_PATH, LEGACY_TEXT_CONFIG )
            .defaultConfig( IndexConfig.BY_TYPE )
            .build();
    }
}
