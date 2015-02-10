package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.InlinesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;

abstract class AbstractCommand
{
    protected ContentTypeRegistry registry;

    protected MixinService mixinService;

    protected final ContentType transformInlines( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTransformInlines( ContentTypes.from( contentType ) );
        return contentTypes.get( 0 );
    }

    protected final ContentTypes transformInlines( final ContentTypes contentTypes )
    {
        return doTransformInlines( contentTypes );
    }

    private final ContentTypes doTransformInlines( final ContentTypes contentTypes )
    {
        final InlinesToFormItemsTransformer transformer = new InlinesToFormItemsTransformer( this.mixinService );
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
