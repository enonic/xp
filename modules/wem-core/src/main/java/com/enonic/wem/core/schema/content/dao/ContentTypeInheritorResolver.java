package com.enonic.wem.core.schema.content.dao;


import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;

class ContentTypeInheritorResolver
{

    private final ContentTypes all;

    ContentTypeInheritorResolver( final ContentTypes all )
    {
        this.all = all;
    }

    ContentTypeNames resolveInheritors( final ContentType contentType )
    {
        final ContentTypeNames.Builder builder = ContentTypeNames.newContentTypeNames();
        for ( final ContentType potentialInheritor : all )
        {
            if ( potentialInheritor.inherit( contentType.getQualifiedName() ) )
            {
                builder.add( potentialInheritor.getQualifiedName() );
            }
        }
        return builder.build();
    }
}
