package com.enonic.wem.portal.internal.content;

public final class ContentResourceProvider
    extends RenderBaseResourceProvider<ContentResource>
{
    @Override
    public Class<ContentResource> getType()
    {
        return ContentResource.class;
    }

    @Override
    public ContentResource newResource()
    {
        final ContentResource instance = new ContentResource();
        configure( instance );
        return instance;
    }
}
