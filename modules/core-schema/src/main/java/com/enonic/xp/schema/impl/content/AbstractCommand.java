package com.enonic.xp.schema.impl.content;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.InlineMixinsToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;

abstract class AbstractCommand
{
    protected ContentTypeRegistry registry;

    protected MixinService mixinService;

    protected final ContentType transformInlineMixins( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTransformInlineMixins( ContentTypes.from( contentType ) );
        return contentTypes.get( 0 );
    }

    protected final ContentTypes transformInlineMixins( final ContentTypes contentTypes )
    {
        return doTransformInlineMixins( contentTypes );
    }

    private final ContentTypes doTransformInlineMixins( final ContentTypes contentTypes )
    {
        final InlineMixinsToFormItemsTransformer transformer = new InlineMixinsToFormItemsTransformer( this.mixinService );
        final ContentTypes.Builder transformedContentTypes = ContentTypes.newContentTypes();
        for ( final ContentType contentType : contentTypes )
        {
            final Form transformedForm = transformer.transformForm( contentType.form() );
            final ContentType transformedCty = ContentType.newContentType( contentType ).form( transformedForm ).build();
            transformedContentTypes.add( transformedCty );
        }
        return transformedContentTypes.build();
    }
}
