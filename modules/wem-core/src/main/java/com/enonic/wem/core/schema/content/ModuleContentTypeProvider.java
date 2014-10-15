package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypes;

public final class ModuleContentTypeProvider
    implements ContentTypeProvider
{
    private final Module module;

    public ModuleContentTypeProvider( final Module module )
    {
        this.module = module;
    }

    @Override
    public ContentTypes get()
    {
        return new ContentTypeLoader().loadContentTypes( module );
    }

}
