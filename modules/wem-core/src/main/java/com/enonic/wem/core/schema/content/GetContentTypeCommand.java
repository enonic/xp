package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.mixin.MixinService;

final class GetContentTypeCommand
    extends AbstractContentTypeCommand
{
    private GetContentTypeParams params;

    ContentType execute()
    {
        params.validate();

        return doExecute();
    }

    private ContentType doExecute()
    {
        final ContentType contentType = registry.getContentType( this.params.getContentTypeName() );
        if ( contentType == null )
        {
            return null;
        }

        if ( !this.params.isMixinReferencesToFormItems() )
        {
            return contentType;
        }
        else
        {
            return transformMixinReferences( contentType );
        }
    }

    GetContentTypeCommand params( final GetContentTypeParams params )
    {
        this.params = params;
        return this;
    }

    GetContentTypeCommand registry( final ContentTypeRegistry registry )
    {
        super.registry = registry;
        return this;
    }

    GetContentTypeCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
