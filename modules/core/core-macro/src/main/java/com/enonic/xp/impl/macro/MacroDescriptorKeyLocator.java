package com.enonic.xp.impl.macro;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.io.Files;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class MacroDescriptorKeyLocator
{
    private final ResourceService service;

    private final String path;

    private final String pattern;

    public MacroDescriptorKeyLocator( final ResourceService service, final String path )
    {
        this.service = service;
        this.path = path;
        this.pattern = this.path + "/.+\\.(xml|js)";
    }

    public Set<MacroKey> findKeys( final ApplicationKey key )
    {
        if ( !service.getResource( ResourceKey.from( key, path ) ).exists() )
        {
            return Collections.emptySet();
        }

        final Set<MacroKey> keys = new LinkedHashSet<>();
        for ( final ResourceKey resource : this.service.findFiles( key, this.pattern ) )
        {
            final MacroKey descriptorKey = newDescriptorKey( key, resource );
            if ( descriptorKey != null )
            {
                keys.add( descriptorKey );
            }
        }

        return keys;
    }

    private MacroKey newDescriptorKey( final ApplicationKey appKey, final ResourceKey key )
    {
        final String nameWithExt = key.getName();
        final String nameWithoutExt = Files.getNameWithoutExtension( nameWithExt );

        if ( key.getPath().equals( this.path + "/" + nameWithoutExt + "/" + nameWithExt ) )
        {
            return MacroKey.from( appKey, nameWithoutExt );
        }

        return null;
    }
}
