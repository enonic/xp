package com.enonic.wem.core.schema.content.dao;


import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeInheritorResolver
{

    private final ContentTypes all;

    public ContentTypeInheritorResolver( final ContentTypes all )
    {
        this.all = all;
    }

    public ContentTypeNames resolveInheritors( final ContentType contentType )
    {
        final ContentTypeNames.Builder builder = ContentTypeNames.newContentTypeNames();
        for ( final ContentType potentialInheritor : all )
        {
            if ( potentialInheritor.inherit( contentType.getContentTypeName() ) )
            {
                builder.add( potentialInheritor.getContentTypeName() );
            }
        }
        return builder.build();
    }
}
