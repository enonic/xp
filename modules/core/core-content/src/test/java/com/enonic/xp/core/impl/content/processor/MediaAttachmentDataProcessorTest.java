package com.enonic.xp.core.impl.content.processor;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class MediaAttachmentDataProcessorTest
{

    final MediaAttachmentDataProcessor processor = new MediaAttachmentDataProcessor();

    @Test
    public void create_set_content_data()
        throws Exception
    {
        final CreateAttachments createAttachments = CreateAttachments.create().
            add( CreateAttachment.create().
                name( "myAtt" ).
                byteSource( ByteSource.wrap( "this is stuff".getBytes() ) ).
                text( "This is the text" ).
                build() ).
            build();

        final CreateContentParams params = CreateContentParams.create().
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.codeMedia() ).
            createAttachments( createAttachments ).
            build();

        final ProcessCreateParams processCreateParams = new ProcessCreateParams( params, MediaInfo.create().
            build() );

        final ProcessCreateResult processCreateResult = this.processor.processCreate( processCreateParams );

        final String attachmentText = processCreateResult.getCreateContentParams().getData().getString(
            MediaAttachmentDataProcessor.CONTENT_DATA_MEDIA_TEXT_PROPERTY );

        assertEquals( "This is the text", attachmentText );
    }

    @Test
    public void update_editor_changes_text_property()
        throws Exception
    {

        final PropertyTree data = new PropertyTree();
        data.setString( MediaAttachmentDataProcessor.CONTENT_DATA_MEDIA_TEXT_PROPERTY, "This is the old text" );

        EditableContent editableContent = new EditableContent( Content.create().
            name( "myContentName" ).
            parentPath( ContentPath.ROOT ).
            data( data ).
            build() );

        final CreateAttachments createAttachments = CreateAttachments.create().
            add( CreateAttachment.create().
                byteSource( ByteSource.wrap( "this is my byteSource".getBytes() ) ).
                label( "myLabel" ).
                mimeType( "myMimeType" ).
                text( "This is the text" ).
                name( "myName" ).
                build() ).
            build();

        final ProcessUpdateResult result = this.processor.processUpdate( ProcessUpdateParams.create().
            contentType( ContentType.create().
                superType( ContentTypeName.media() ).
                name( "myContent" ).
                build() ).
            createAttachments( createAttachments ).
            build() );

        result.getEditor().edit( editableContent );

        final String textValue = editableContent.data.getString( MediaAttachmentDataProcessor.CONTENT_DATA_MEDIA_TEXT_PROPERTY );

        assertEquals( "This is the text", textValue );
    }
}