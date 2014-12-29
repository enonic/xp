package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;


final class GetChildContentTypesCommand
    extends AbstractContentTypeCommand
{
    private GetChildContentTypesParams params;

    ContentTypes execute()
    {
        params.validate();

        return doExecute();
    }

    private ContentTypes doExecute()
    {
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();
        final ContentTypes allContentTypes = registry.getAllContentTypes();

        for ( ContentType contentType : allContentTypes )
        {
            if ( params.getParentName().equals( contentType.getSuperType() ) )
            {
                builder.add( contentType );
            }
        }
        return builder.build();
    }

    GetChildContentTypesCommand params( final GetChildContentTypesParams params )
    {
        this.params = params;
        return this;
    }

    GetChildContentTypesCommand registry( final ContentTypeRegistry registry )
    {
        super.registry = registry;
        return this;
    }

    GetChildContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
