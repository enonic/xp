package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentTypes;

final class GetAllContentTypesCommand
    extends AbstractCommand
{

    public ContentTypes execute()
    {
        final ContentTypes contentTypes = this.registry.getAll();

        return transformInlineMixins( contentTypes );
    }
}
