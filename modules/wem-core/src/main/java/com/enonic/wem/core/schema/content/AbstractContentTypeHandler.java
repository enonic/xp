package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

public abstract class AbstractContentTypeHandler<T extends Command>
    extends CommandHandler<T>
{
    protected ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    protected ContentType transformMixinReferences( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTranformMixinReferences( ContentTypes.from( contentType ) );

        return contentTypes.get( 0 );
    }

    protected ContentTypes transformMixinReferences( final ContentTypes contentTypes )
    {
        return doTranformMixinReferences( contentTypes );
    }

    private ContentTypes doTranformMixinReferences( final ContentTypes contentTypes )
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
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );

        for ( final ContentType contentType : contentTypes )
        {
            builder.add( populateInheritors( contentTypeInheritorResolver, contentType ) );
        }
        return builder.build();
    }

    private ContentType populateInheritors( final ContentTypeInheritorResolver contentTypeInheritorResolver, ContentType contentType )
    {
        contentType = ContentType.newContentType( contentType ).
            inheritors( contentTypeInheritorResolver.resolveInheritors( contentType.getName() ).isNotEmpty() ).
            build();
        return contentType;
    }

    protected void populateInheritors( final ContentTypeInheritorResolver contentTypeInheritorResolver,
                                       final ContentType.Builder contentType, final ContentTypeName contentTypeName )
    {
        contentType.
            inheritors( contentTypeInheritorResolver.resolveInheritors( contentTypeName ).isNotEmpty() );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Inject
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
