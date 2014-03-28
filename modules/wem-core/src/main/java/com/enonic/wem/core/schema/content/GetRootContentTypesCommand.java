package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

final class GetRootContentTypesCommand
    extends AbstractContentTypeCommand
{
    ContentTypes execute()
    {
        final ContentTypes allContentTypes = contentTypeDao.getAllContentTypes();
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();

        for ( ContentType contentType : allContentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                builder.add( contentType );
            }
        }

        return populateInheritors( builder.build() );
    }

    GetRootContentTypesCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    GetRootContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
