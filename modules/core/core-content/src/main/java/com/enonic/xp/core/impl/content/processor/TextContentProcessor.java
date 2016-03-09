package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;


@Component
public class TextContentProcessor
    implements ContentProcessor
{
    private MixinService mixinService;

    private ContentTypeService contentTypeService;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isTextContainingMedia();
    }

    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( createContentParams.getType() ) );

        final CreateAttachments originalAttachments = createContentParams.getCreateAttachments();

        final CreateAttachment sourceAttachment = originalAttachments.first();

        final Mixins contentMixins = mixinService.getByContentType( contentType );
        ExtraDatas extraDatas = null;

        if ( params.getMediaInfo() != null )
        {
            extraDatas = extractMetadata( params.getMediaInfo(), contentMixins );
        }

        final CreateAttachments.Builder builder = CreateAttachments.create();
        builder.add( sourceAttachment );

        final CreateContentParams newCreateContentParams = CreateContentParams.create( params.getCreateContentParams() ).
            createAttachments( builder.build() ).
            extraDatas( extraDatas ).
            build();

        return new ProcessCreateResult( newCreateContentParams );

    }

    private ExtraDatas extractMetadata( MediaInfo mediaInfo, Mixins mixins )
    {
        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();

        for ( Mixin mixin : mixins )
        {
            handleMixin( extradatasBuilder, mixin, mediaInfo.getTextContent() );
        }

        return extradatasBuilder.build();
    }

    private void handleMixin( final ExtraDatas.Builder extradatasBuilder, final Mixin mixin, final String textContent )
    {
        final String formItemName = MediaInfo.EXTRACTED_TEXT_CONTENT;

        final FormItem formItem = mixin.getForm().getFormItems().getItemByName( formItemName );

        if ( formItem == null )
        {
            return;
        }

        final ExtraData extraData = new ExtraData( mixin.getName(), new PropertyTree() );

        extraData.getData().addString( formItemName, textContent );

        extradatasBuilder.add( extraData );
    }


    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
