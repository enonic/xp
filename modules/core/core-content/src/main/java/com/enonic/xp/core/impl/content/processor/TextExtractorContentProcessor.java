package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.media.ExtractedTextInfo;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;


@Component
public class TextExtractorContentProcessor
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
        final CreateContentParams originalCreateContentParams = params.getCreateContentParams();
        final ContentTypeName contentTypeName = originalCreateContentParams.getType();

        final Mixins contentTypeMixins = getMixins( contentTypeName );

        ExtraDatas extraDatas = null;

        if ( params.getMediaInfo() != null )
        {
            extraDatas = extractMetadata( params.getMediaInfo(), contentTypeMixins );
        }

        final CreateContentParams newCreateContentParams = CreateContentParams.create( originalCreateContentParams ).
            extraDatas( extraDatas ).
            build();

        return new ProcessCreateResult( newCreateContentParams );
    }

    private Mixins getMixins( final ContentTypeName contentTypeName )
    {
        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );

        return mixinService.getByContentType( contentType );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        //final Mixins contentTypeMixins = getMixins( params.getUpdateContentParams(). );
        return null;
    }

    private ExtraDatas extractMetadata( final MediaInfo mediaInfo, final Mixins mixins )
    {
        final ExtraDatas.Builder extraDatasBuilder = ExtraDatas.create();

        for ( Mixin mixin : mixins )
        {
            populateTextExtractionFormItemsInMixin( extraDatasBuilder, mixin, mediaInfo.getExtractedTextInfo() );
        }

        return extraDatasBuilder.build();
    }

    private void populateTextExtractionFormItemsInMixin( final ExtraDatas.Builder extradatasBuilder, final Mixin mixin,
                                                         final ExtractedTextInfo extractedTextInfo )
    {
        final FormItems formItems = mixin.getForm().getFormItems();

        if ( formItems.size() == 0 )
        {
            return;
        }

        final ExtraData extraData = new ExtraData( mixin.getName(), new PropertyTree() );

        final FormItem extractedTextItem = formItems.getItemByName( MediaInfo.EXTRACTED_TEXT_CONTENT );

        if ( extractedTextItem != null )
        {
            final String extractedText = ExtractedTextCleaner.clean( extractedTextInfo.getExtractedText() );
            extraData.getData().addString( MediaInfo.EXTRACTED_TEXT_CONTENT, extractedText );
        }

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
