package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.schema.content.ContentTypes;

public class GetAllContentTypesHandler
    extends AbstractContentTypeHandler<GetAllContentTypes>
{

    @Override
    public void handle()
        throws Exception
    {
        final ContentTypes allContentTypes = getAllContentTypes();

        final ContentTypes contentTypes = populateInheritors( allContentTypes );

        if ( !command.isMixinReferencesToFormItems() )
        {
            command.setResult( contentTypes );
        }
        else
        {
            command.setResult( transformMixinReferences( contentTypes ) );
        }
    }
}
