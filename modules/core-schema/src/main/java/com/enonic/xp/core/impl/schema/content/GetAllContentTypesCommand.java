package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;

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
