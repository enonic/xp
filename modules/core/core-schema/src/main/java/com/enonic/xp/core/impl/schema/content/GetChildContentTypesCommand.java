package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetChildContentTypesParams;

final class GetChildContentTypesCommand
    extends AbstractCommand
{
    protected GetChildContentTypesParams params;

    public ContentTypes execute()
    {
        this.params.validate();
        return doExecute();
    }

    private ContentTypes doExecute()
    {
        final ContentTypes.Builder builder = ContentTypes.create();
        final ContentTypes allContentTypes = registry.getAll();

        for ( final ContentType contentType : allContentTypes )
        {
            if ( this.params.getParentName().equals( contentType.getSuperType() ) )
            {
                builder.add( contentType );
            }
        }
        return builder.build();
    }
}
