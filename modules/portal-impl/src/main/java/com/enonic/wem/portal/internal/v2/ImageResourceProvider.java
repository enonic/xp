package com.enonic.wem.portal.internal.v2;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.jaxrs.ResourceProvider;

@Component(immediate = true)
public final class ImageResourceProvider
    implements ResourceProvider<ImageResource>
{
    @Override
    public Class<ImageResource> getType()
    {
        return ImageResource.class;
    }

    @Override
    public ImageResource newResource()
    {
        return new ImageResource();
    }
}
