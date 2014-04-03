package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


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
        final ContentTypes allContentTypes = contentTypeDao.getAllContentTypes();

        for ( ContentType contentType : allContentTypes )
        {
            if ( params.getParentName().equals( contentType.getSuperType() ) )
            {
                builder.add( contentType );
            }
        }
        return populateInheritors( builder.build() );
    }

    GetChildContentTypesCommand params( final GetChildContentTypesParams params )
    {
        this.params = params;
        return this;
    }

    GetChildContentTypesCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    GetChildContentTypesCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
