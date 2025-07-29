package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.mixin.MixinService;

abstract class AbstractCommand
{
    protected ContentTypeRegistry registry;

    protected MixinService mixinService;

    protected final ContentType transformInlineMixins( final ContentType contentType )
    {
        return ContentType.create( contentType ).form( mixinService.inlineFormItems( contentType.getForm() ) ).build();
    }

    protected final ContentTypes transformInlineMixins( final ContentTypes contentTypes )
    {
        return contentTypes.stream().map( this::transformInlineMixins ).collect( ContentTypes.collector() );
    }
}
