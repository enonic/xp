package com.enonic.xp.core.impl.content.page;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceService;

public final class OptionalDescriptorKeyLocator
{
    private final ResourceService service;

    private final String path;

    public OptionalDescriptorKeyLocator( final ResourceService service, final String path )
    {
        this.service = service;
        this.path = path;
    }

    public List<DescriptorKey> findKeys( final ApplicationKey key )
    {
        return this.service.findFolders( key, this.path ).
            stream().
            map( resourceKey -> DescriptorKey.from( key, resourceKey.getName() ) ).
            collect( Collectors.toList() );
    }
}
