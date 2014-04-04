package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

abstract class AbstractContentTypeCommand
{
    protected ContentTypeDao contentTypeDao;

    protected MixinService mixinService;

    protected ContentType transformMixinReferences( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTransformMixinReferences( ContentTypes.from( contentType ) );

        return contentTypes.get( 0 );
    }

    protected ContentTypes transformMixinReferences( final ContentTypes contentTypes )
    {
        return doTransformMixinReferences( contentTypes );
    }

    private ContentTypes doTransformMixinReferences( final ContentTypes contentTypes )
    {
        final MixinReferencesToFormItemsTransformer transformer = new MixinReferencesToFormItemsTransformer( mixinService );

        ContentTypes.Builder transformedContentTypes = ContentTypes.newContentTypes();
        for ( final ContentType contentType : contentTypes )
        {
            final Form transformedForm = transformer.transformForm( contentType.form() );
            final ContentType transformedCty = ContentType.newContentType( contentType ).form( transformedForm ).build();
            transformedContentTypes.add( transformedCty );
        }
        return transformedContentTypes.build();
    }

    protected ContentTypes populateInheritors( final ContentTypes contentTypes )
    {
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();

        final ContentTypes allContentTypes = this.contentTypeDao.getAllContentTypes();
        final ContentTypeInheritorResolver resolver = new ContentTypeInheritorResolver( allContentTypes );

        for ( final ContentType contentType : contentTypes )
        {
            builder.add( populateInheritors( resolver, contentType ) );
        }
        return builder.build();
    }

    private ContentType populateInheritors( final ContentTypeInheritorResolver resolver, ContentType contentType )
    {
        contentType = ContentType.newContentType( contentType ).
            inheritors( resolver.resolveInheritors( contentType.getName() ).isNotEmpty() ).
            build();
        return contentType;
    }

    protected void populateInheritors( final ContentTypeInheritorResolver resolver,
                                       final ContentType.Builder contentType,
                                       final ContentTypeName contentTypeName )
    {
        contentType.inheritors( resolver.resolveInheritors( contentTypeName ).isNotEmpty() );
    }
}
