package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;

@Component(immediate = true)
public class MediaAttachmentDataProcessor
    implements ContentProcessor
{
    public static final String CONTENT_DATA_MEDIA_TEXT_PROPERTY = "text";

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isMedia() || contentType.getName().isDescendantOfMedia();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final PropertyTree contentData = populateDataWithAttachmentText( params, createContentParams );

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            createAttachments( removeTextFromAttachments( params.getCreateContentParams().getCreateAttachments() ) ).
            contentData( contentData ).
            build() );
    }

    private PropertyTree populateDataWithAttachmentText( final ProcessCreateParams params, final CreateContentParams createContentParams )
    {
        final PropertyTree contentData = createContentParams.getData();

        params.getCreateContentParams().getCreateAttachments().forEach( ( attachment ) -> {
            contentData.addString( CONTENT_DATA_MEDIA_TEXT_PROPERTY, attachment.getTextContent() );
        } );
        return contentData;
    }

    private CreateAttachments removeTextFromAttachments( final CreateAttachments createAttachments )
    {
        if ( createAttachments == null )
        {
            return null;
        }

        final CreateAttachments.Builder newCreateAttachments = CreateAttachments.create();

        createAttachments.forEach( ( attachment ) -> {
            newCreateAttachments.add( CreateAttachment.create( attachment ).
                text( null ).
                build() );
        } );
        return newCreateAttachments.build();
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        return new ProcessUpdateResult( removeTextFromAttachments( params.getCreateAttachments() ),
                                        new AttachmentEditor( params.getCreateAttachments() ) );
    }

    private class AttachmentEditor
        implements ContentEditor
    {
        private final CreateAttachments createAttachments;

        public AttachmentEditor( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
        }

        @Override
        public void edit( final EditableContent edit )
        {
            if ( createAttachments == null )
            {
                return;
            }

            // Should probably do some comparison of existing attachments here? Works now since media have no
            // attachments

            edit.data.removeProperty( CONTENT_DATA_MEDIA_TEXT_PROPERTY );

            createAttachments.forEach( ( attachment ) -> {
                edit.data.addString( CONTENT_DATA_MEDIA_TEXT_PROPERTY, attachment.getTextContent() );
            } );

        }
    }

}
