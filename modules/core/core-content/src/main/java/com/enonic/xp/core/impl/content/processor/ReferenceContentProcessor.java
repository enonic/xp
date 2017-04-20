package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public final class ReferenceContentProcessor
    implements ContentProcessor
{
    private Parser<ContentIds> parser;

    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();
        final PropertyTree data = createContentParams.getData();

        final ContentType contentType =
            contentTypeService.getByName( new GetContentTypeParams().contentTypeName( createContentParams.getType() ) );

        processContentData( contentType.getForm(), data, false );
        processMixins( params.getCreateContentParams().getExtraDatas(), false );

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            contentData( data ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final ContentEditor editor = editable -> {

            processContentData( params.getContentType().getForm(), editable.data, true );
            processMixins( editable.extraDatas, true );

        };
        return new ProcessUpdateResult( createAttachments, editor );
    }

    private void processMixins( final ExtraDatas extraDatas, final boolean removeExisting )
    {
        if ( extraDatas == null )
        {
            return;
        }

        for ( final ExtraData extraData : extraDatas )
        {
            final Mixin mixin = mixinService.getByName( extraData.getName() );

            doTraverseProperties( mixin.getForm(), extraData.getData(), removeExisting );
        }
    }

    private void processContentData( final Form form, final PropertyTree data, final boolean removeExisting )
    {
        final Form inlinedForm = this.mixinService.inlineFormItems( form );
        doTraverseProperties( inlinedForm, data, removeExisting );
    }

    private void doTraverseProperties( final Form form, final PropertyTree data, final boolean removeExisting )
    {
        final ReferenceVisitor visitor = new ReferenceVisitor( data, parser );

        if ( removeExisting )
        {
            visitor.removeOldReferences();
        }

        visitor.traverse( form );
    }

    @Override
    public boolean supports( final ContentType contentType )
    {
        return true;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setParser( final Parser<ContentIds> parser )
    {
        this.parser = parser;
    }
}
