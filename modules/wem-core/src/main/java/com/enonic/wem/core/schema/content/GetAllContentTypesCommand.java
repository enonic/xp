package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

final class GetAllContentTypesCommand
    extends AbstractContentTypeCommand
{
    private GetAllContentTypesParams params;

    ContentTypes execute()
    {
        final ContentTypes contentTypes = contentTypeDao.getAllContentTypes();

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

    GetAllContentTypesCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    GetAllContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
