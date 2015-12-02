package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.mixin.MixinService;

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

    private ContentTypes doTransformInlineMixins( final ContentTypes contentTypes )
    {
        final ContentTypes.Builder transformedContentTypes = ContentTypes.create();
        for ( final ContentType contentType : contentTypes )
        {
            final Form transformedForm = mixinService.inlineFormItems( contentType.getForm() );
            final ContentType transformedCty = ContentType.create( contentType ).form( transformedForm ).build();
            transformedContentTypes.add( transformedCty );
        }
        return transformedContentTypes.build();
    }
}
