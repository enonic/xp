package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.DeleteContentTypeParams;
import com.enonic.wem.api.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.UnableToDeleteContentTypeException;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


final class DeleteContentTypeCommand
    extends AbstractContentTypeCommand
{
    private DeleteContentTypeParams params;

    DeleteContentTypeResult execute()
    {
        params.validate();

        return doExecute();
    }

    private DeleteContentTypeResult doExecute()
    {
        final ContentTypes allContentTypes = this.contentTypeDao.getAllContentTypes();
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );
        final ContentTypeNames inheritors = contentTypeInheritorResolver.resolveInheritors( params.getName() );
        if ( inheritors.isNotEmpty() )
        {
            throw new UnableToDeleteContentTypeException( params.getName(), "Inheritors found: " + inheritors.toString() );
        }

        final ContentType.Builder deletedContentType = contentTypeDao.getContentType( params.getName() );
        contentTypeDao.deleteContentType( params.getName() );
        return new DeleteContentTypeResult( deletedContentType != null ? deletedContentType.build() : null );
    }

    DeleteContentTypeCommand params( final DeleteContentTypeParams params )
    {
        this.params = params;
        return this;
    }

    DeleteContentTypeCommand contentTypeDao( final ContentTypeDao contentTypeDao )
    {
        super.contentTypeDao = contentTypeDao;
        return this;
    }

    DeleteContentTypeCommand mixinService( final MixinService mixinService )
    {
        super.mixinService = mixinService;
        return this;
    }
}
