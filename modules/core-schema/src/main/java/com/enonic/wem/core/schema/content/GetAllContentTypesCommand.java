package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;

final class GetAllContentTypesCommand
    extends AbstractCommand
{
    protected GetAllContentTypesParams params;

    public ContentTypes execute()
    {
        final ContentTypes contentTypes = this.registry.getAll();
        if ( !this.params.isInlinesToFormItems() )
        {
            return contentTypes;
        }
        else
        {
            return transformInlines( contentTypes );
        }
    }
}
