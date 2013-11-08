package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeInheritorResolver;

public abstract class AbstractContentTypeHandler<T extends Command>
    extends CommandHandler<T>
{
    final static ContentTypeNodeTranslator CONTENT_TYPE_NODE_TRANSLATOR = new ContentTypeNodeTranslator();

    ContentTypes getAllContentTypes()
    {
        final Nodes nodes = context.getClient().execute( Commands.node().get().byParent( new NodePath( "/content-types" ) ) );

        return CONTENT_TYPE_NODE_TRANSLATOR.fromNodes( nodes );
    }

    ContentType transformMixinReferences( final ContentType contentType )
    {
        final ContentTypes contentTypes = doTranformMixinReferences( ContentTypes.from( contentType ) );

        return contentTypes.get( 0 );
    }

    ContentTypes transformMixinReferences( final ContentTypes contentTypes )
    {
        return doTranformMixinReferences( contentTypes );
    }

    private ContentTypes doTranformMixinReferences( final ContentTypes contentTypes )
    {
        final MixinReferencesToFormItemsTransformer transformer = new MixinReferencesToFormItemsTransformer( context.getClient() );

        ContentTypes.Builder transformedContentTypes = ContentTypes.newContentTypes();
        for ( final ContentType contentType : contentTypes )
        {
            final Form transformedForm = transformer.transformForm( contentType.form() );
            final ContentType transformedCty = ContentType.newContentType( contentType ).form( transformedForm ).build();
            transformedContentTypes.add( transformedCty );
        }
        return transformedContentTypes.build();
    }


    protected ContentType appendInheritors( final ContentTypeInheritorResolver contentTypeInheritorResolver, ContentType contentType )
    {
        contentType = ContentType.newContentType( contentType ).
            inheritors( contentTypeInheritorResolver.resolveInheritors( contentType ).isNotEmpty() ).
            build();
        return contentType;
    }
}
