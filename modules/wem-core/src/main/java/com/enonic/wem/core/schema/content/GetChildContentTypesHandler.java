package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetChildContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;


public class GetChildContentTypesHandler
    extends AbstractContentTypeHandler<GetChildContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes.Builder builder = ContentTypes.newContentTypes();

        final ContentTypeName name = command.getParentName();

        final ContentTypes allContentTypes = getAllContentTypes();

        for ( ContentType contentType : allContentTypes )
        {
            if ( name.equals( contentType.getSuperType() ) )
            {
                builder.add( contentType );
            }
        }
        command.setResult( populateInheritors( builder.build() ) );
    }

}
