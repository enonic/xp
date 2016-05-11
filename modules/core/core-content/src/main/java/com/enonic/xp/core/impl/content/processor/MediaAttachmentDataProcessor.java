package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;

@Component(immediate = true)
public class MediaAttachmentDataProcessor
    implements ContentProcessor
{

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isDescendantOfMedia() || contentType.getName().isDescendantOfMedia();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final CreateAttachments createAttachments = createContentParams.getCreateAttachments();

        final CreateAttachments.Builder newCreateAttachments = CreateAttachments.create();

        final PropertyTree contentData = createContentParams.getData();

        for ( final CreateAttachment createAttachment : createAttachments )
        {
            contentData.addString( "text", createAttachment.getTextContent() );

            newCreateAttachments.add( CreateAttachment.create( createAttachment ).
                text( null ).
                build() );
        }

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            createAttachments( newCreateAttachments.build() ).
            contentData( contentData ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        return null;
    }
}
