package com.enonic.xp.core.impl.content.page;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public final class DescriptorKeyLocator
{
    private final ResourceService service;

    private final String path;

    private final Pattern pattern;

    public DescriptorKeyLocator( final ResourceService service, final String path )
    {
        this.service = service;
        this.path = path;
        this.pattern = Pattern.compile( this.path + "/([^/]+)/([^/]+)\\.xml" );
    }

    public List<DescriptorKey> findKeys( final ApplicationKey key )
    {
        final List<DescriptorKey> keys = Lists.newArrayList();
        for ( final ResourceKey resource : this.service.findFiles( key, this.path, "xml", true ) )
        {
            final Matcher matcher = this.pattern.matcher( resource.getPath() );
            if ( matcher.matches() )
            {
                final String name = matcher.group( 2 );
                if ( name.equals( matcher.group( 1 ) ) )
                {
                    keys.add( DescriptorKey.from( key, name ) );
                }
            }
        }

        return keys;
    }
}
