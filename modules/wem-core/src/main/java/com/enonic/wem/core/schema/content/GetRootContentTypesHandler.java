package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetRootContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;


public class GetRootContentTypesHandler
    extends AbstractContentTypeHandler<GetRootContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes allContentTypes = getAllContentTypes();

        ContentTypes.Builder builder = ContentTypes.newContentTypes();

        final ContentTypes contentTypes = allContentTypes;

        for ( ContentType contentType : contentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                builder.add( contentType );
            }
        }

        final ContentTypes rootContentTypes = populateInheritors( builder.build() );

        command.setResult( rootContentTypes );
    }

}
