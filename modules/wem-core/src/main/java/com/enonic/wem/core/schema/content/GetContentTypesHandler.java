package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;


public final class GetContentTypesHandler
    extends AbstractContentTypeHandler<GetContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes contentTypes = getContentTypes( command.getContentTypeNames() );
        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentTypes );
        }
        else
        {
            command.setResult( transformMixinReferences( contentTypes ) );
        }
    }

    private ContentTypes getContentTypes( final ContentTypeNames contentTypeNames )
    {
        final ContentTypes.Builder contentTypes = ContentTypes.newContentTypes();
        for ( ContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType.Builder contentTypeBuilder = contentTypeDao.getContentType( contentTypeName );
            if ( contentTypeBuilder != null )
            {
                final ContentTypes allContentTypes = contentTypeDao.getAllContentTypes();
                final ContentTypeInheritorResolver contentTypeInheritorResolver = new ContentTypeInheritorResolver( allContentTypes );
                populateInheritors( contentTypeInheritorResolver, contentTypeBuilder, contentTypeName );
                contentTypes.add( contentTypeBuilder.build() );
            }
        }
        return contentTypes.build();
    }

}
