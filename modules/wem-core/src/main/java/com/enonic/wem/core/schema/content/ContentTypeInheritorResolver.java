package com.enonic.wem.core.schema.content;


import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;

public class ContentTypeInheritorResolver
{
    private final ContentTypes allContentTypes;

    protected ContentTypeInheritorResolver( final ContentTypes allContentTypes )
    {
        this.allContentTypes = allContentTypes;
    }

    protected ContentTypeNames resolveInheritors( final ContentTypeName contentType )
    {
        final ContentTypeNames.Builder builder = ContentTypeNames.newContentTypeNames();
        for ( final ContentType potentialInheritor : this.allContentTypes )
        {
            if ( potentialInheritor.inherit( contentType ) )
            {
                builder.add( potentialInheritor.getName() );
            }
        }
        return builder.build();
    }
}
