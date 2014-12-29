package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;

final class GetRootContentTypesCommand
    extends AbstractContentTypeCommand
{
    ContentTypes execute()
    {
        final ContentTypes allContentTypes = registry.getAllContentTypes();
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();

        for ( ContentType contentType : allContentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                builder.add( contentType );
            }
        }

        return builder.build();
    }

    GetRootContentTypesCommand registry( final ContentTypeRegistry registry )
    {
        super.registry = registry;
        return this;
    }

    GetRootContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
