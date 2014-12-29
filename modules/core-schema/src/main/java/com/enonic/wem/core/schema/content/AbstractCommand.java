package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;

abstract class AbstractCommand
{
    protected ContentTypeRegistry registry;

    protected MixinService mixinService;

    protected final ContentType transformMixinReferences( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTransformMixinReferences( ContentTypes.from( contentType ) );
        return contentTypes.get( 0 );
    }

    protected final ContentTypes transformMixinReferences( final ContentTypes contentTypes )
    {
        return doTransformMixinReferences( contentTypes );
    }

    private final ContentTypes doTransformMixinReferences( final ContentTypes contentTypes )
    {
        final MixinReferencesToFormItemsTransformer transformer = new MixinReferencesToFormItemsTransformer( this.mixinService );
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
