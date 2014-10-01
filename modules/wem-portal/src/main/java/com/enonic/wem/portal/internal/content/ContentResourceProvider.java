package com.enonic.wem.portal.internal.content;

public final class ContentResourceProvider
    extends RenderBaseResourceProvider<ContentResource2>
{
    @Override
    public Class<ContentResource2> getType()
    {
        return ContentResource2.class;
    }

    @Override
    public ContentResource2 newResource()
    {
        final ContentResource2 instance = new ContentResource2();
        configure( instance );
        return instance;
    }
}
