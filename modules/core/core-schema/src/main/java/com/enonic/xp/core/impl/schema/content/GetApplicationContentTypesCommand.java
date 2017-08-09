package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentTypes;

final class GetApplicationContentTypesCommand
    extends AbstractCommand
{
    boolean inlineMixinsToFormItems;

    ApplicationKey applicationKey;

    ContentTypes execute()
    {
        final ContentTypes contentTypes = this.registry.getByApplication( applicationKey );
        if ( !this.inlineMixinsToFormItems )
        {
            return contentTypes;
        }
        else
        {
            return transformInlineMixins( contentTypes );
        }
    }
}
