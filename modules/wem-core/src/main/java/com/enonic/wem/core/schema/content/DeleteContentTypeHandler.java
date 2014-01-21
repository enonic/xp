package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.UnableToDeleteContentTypeException;


public final class DeleteContentTypeHandler
    extends AbstractContentTypeHandler<DeleteContentType>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes allContentTypes = this.contentTypeDao.getAllContentTypes();
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );
        final ContentTypeNames inheritors = contentTypeInheritorResolver.resolveInheritors( command.getName() );
        if ( inheritors.isNotEmpty() )
        {
            throw new UnableToDeleteContentTypeException( command.getName(), "Inheritors found: " + inheritors.toString() );
        }

        final ContentType.Builder deletedContentType = contentTypeDao.getContentType( command.getName() );
        contentTypeDao.deleteContentType( command.getName() );
        command.setResult( new DeleteContentTypeResult( deletedContentType != null ? deletedContentType.build() : null ) );
    }

}
