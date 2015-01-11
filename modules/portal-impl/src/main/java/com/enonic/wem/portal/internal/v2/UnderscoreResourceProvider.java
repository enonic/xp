package com.enonic.wem.portal.internal.v2;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.jaxrs.ResourceProvider;

@Component(immediate = true)
public final class UnderscoreResourceProvider
    implements ResourceProvider<UnderscoreResource>
{
    @Override
    public Class<UnderscoreResource> getType()
    {
        return UnderscoreResource.class;
    }

    @Override
    public UnderscoreResource newResource()
    {
        return new UnderscoreResource();
    }
}
