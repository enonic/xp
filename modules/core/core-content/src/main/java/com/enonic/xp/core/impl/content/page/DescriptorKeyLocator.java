package com.enonic.xp.core.impl.content.page;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public final class DescriptorKeyLocator
{
    private final ResourceService service;

    private final String path;

    private final String pattern;

    public DescriptorKeyLocator( final ResourceService service, final String path, final boolean optional )
    {
        this.service = service;
        this.path = path;
        this.pattern = this.path + "/.+\\.(xml" + ( optional ? "|js" : "" ) + ")";
    }

    public List<DescriptorKey> findKeys( final ApplicationKey key )
    {
        final List<DescriptorKey> keys = Lists.newArrayList();
        for ( final ResourceKey resource : this.service.findFiles( key, this.pattern ) )
        {
            final DescriptorKey descriptorKey = newDescriptorKey( key, resource );
            if ( descriptorKey != null )
            {
                keys.add( descriptorKey );
            }
        }

        return keys;
    }

    private DescriptorKey newDescriptorKey( final ApplicationKey appKey, final ResourceKey key )
    {
        final String nameWithExt = key.getName();
        final String nameWithoutExt = Files.getNameWithoutExtension( nameWithExt );

        if ( key.getPath().equals( this.path + "/" + nameWithoutExt + "/" + nameWithExt ) )
        {
            return DescriptorKey.from( appKey, nameWithoutExt );
        }

        return null;
    }
}
