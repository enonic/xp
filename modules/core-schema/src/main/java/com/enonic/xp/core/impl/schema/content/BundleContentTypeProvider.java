package com.enonic.xp.core.impl.schema.content;

import org.osgi.framework.Bundle;

import com.enonic.xp.core.schema.content.ContentTypeProvider;
import com.enonic.xp.core.schema.content.ContentTypes;

public final class BundleContentTypeProvider
    implements ContentTypeProvider
{
    private final ContentTypes types;

    private BundleContentTypeProvider( final ContentTypes types )
    {
        this.types = types;
    }

    @Override
    public ContentTypes get()
    {
        return this.types;
    }

    public static BundleContentTypeProvider create( final Bundle bundle )
    {
        final ContentTypes types = new ContentTypeLoader( bundle ).load();
        return types != null ? new BundleContentTypeProvider( types ) : null;
    }
}
