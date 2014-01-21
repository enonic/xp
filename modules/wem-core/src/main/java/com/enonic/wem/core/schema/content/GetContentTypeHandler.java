package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNotFoundException;
import com.enonic.wem.api.schema.content.ContentTypes;

public class GetContentTypeHandler
    extends AbstractContentTypeHandler<GetContentType>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentType.Builder contentTypeBuilder = contentTypeDao.getContentType( command.getContentTypeName() );
        if ( contentTypeBuilder == null )
        {
            if ( command.isNotFoundAsException() )
            {
                throw new ContentTypeNotFoundException( command.getContentTypeName() );
            }
            else
            {
                command.setResult( null );
                return;
            }
        }

        final ContentTypes allContentTypes = contentTypeDao.getAllContentTypes();
        final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );
        populateInheritors( contentTypeInheritorResolver, contentTypeBuilder, command.getContentTypeName() );
        final ContentType contentType = contentTypeBuilder.build();

        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentType );
        }
        else
        {
            command.setResult( transformMixinReferences( contentType ) );
        }
    }

}
