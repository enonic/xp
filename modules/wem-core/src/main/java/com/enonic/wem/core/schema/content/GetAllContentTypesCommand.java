package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;

final class GetAllContentTypesCommand
    extends AbstractContentTypeCommand
{
    private GetAllContentTypesParams params;

    ContentTypes execute()
    {
        final ContentTypes contentTypes = registry.getAllContentTypes();

        if ( !params.isMixinReferencesToFormItems() )
        {
            return contentTypes;
        }
        else
        {
            return transformMixinReferences( contentTypes );
        }
    }

    GetAllContentTypesCommand params( final GetAllContentTypesParams params )
    {
        this.params = params;
        return this;
    }

    GetAllContentTypesCommand registry( final ContentTypeRegistry registry )
    {
        super.registry = registry;
        return this;
    }

    GetAllContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
