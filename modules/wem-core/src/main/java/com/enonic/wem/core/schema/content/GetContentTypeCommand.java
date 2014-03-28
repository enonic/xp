package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetContentTypeParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

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
        final ContentType.Builder contentTypeBuilder = contentTypeDao.getContentType( this.params.getContentTypeName() );
        if ( contentTypeBuilder == null )
        {
            if ( this.params.isNotFoundAsException() )
            {
                throw new ContentTypeNotFoundException( this.params.getContentTypeName() );
            }
            else
            {
                return null;
            }
        }

        final ContentTypes allContentTypes = contentTypeDao.getAllContentTypes();
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );
        populateInheritors( contentTypeInheritorResolver, contentTypeBuilder, this.params.getContentTypeName() );
        final ContentType contentType = contentTypeBuilder.build();

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

    GetContentTypeCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    GetContentTypeCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
