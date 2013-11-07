package com.enonic.wem.core.schema.content;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.command.schema.content.GetRootContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;


public class GetRootContentTypesHandler
    extends AbstractGetContentTypeHandler<GetRootContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        ImmutableList.Builder<ContentType> rootContentTypes = ImmutableList.builder();

        final ContentTypes contentTypes = getAllContentTypes();
        for ( ContentType contentType : contentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                rootContentTypes.add( contentType );
            }
        }
        command.setResult( ContentTypes.from( rootContentTypes.build() ) );
    }

}
