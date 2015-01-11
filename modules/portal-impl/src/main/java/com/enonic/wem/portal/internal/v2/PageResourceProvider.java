package com.enonic.wem.portal.internal.v2;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.jaxrs.ResourceProvider;

@Component(immediate = true)
public final class PageResourceProvider
    implements ResourceProvider<PageResource>
{
    @Override
    public Class<PageResource> getType()
    {
        return PageResource.class;
    }

    @Override
    public PageResource newResource()
    {
        return new PageResource();
    }
}
