package com.enonic.xp.schema.impl.content;

import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;

final class GetAllContentTypesCommand
    extends AbstractCommand
{
    protected GetAllContentTypesParams params;

    public ContentTypes execute()
    {
        final ContentTypes contentTypes = this.registry.getAll();
        if ( !this.params.isInlineMixinsToFormItems() )
        {
            return contentTypes;
        }
        else
        {
            return transformInlineMixins( contentTypes );
        }
    }
}
