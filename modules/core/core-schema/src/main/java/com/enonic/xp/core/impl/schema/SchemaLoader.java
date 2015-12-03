package com.enonic.xp.core.impl.schema;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.BaseSchemaName;

public abstract class SchemaLoader<N extends BaseSchemaName>
{
    private final String path;

    private final Pattern pattern;

    private final ResourceService resourceService;

    public SchemaLoader( final ResourceService resourceService, final String path )
    {
        this.resourceService = resourceService;
        this.path = path;
        this.pattern = Pattern.compile( this.path + "/([^/]+)/([^/]+)\\.xml" );
    }

    protected final ResourceKey toResourceKey( final N name, final String ext )
    {
        final ApplicationKey appKey = name.getApplicationKey();
        final String localName = name.getLocalName();
        return ResourceKey.from( appKey, this.path + "/" + localName + "/" + localName + "." + ext );
    }

    protected final Icon loadIcon( final N name )
    {
        final ResourceKey resourceKey = toResourceKey( name, "png" );
        final Resource resource = this.resourceService.getResource( resourceKey );
        return SchemaHelper.loadIcon( resource );
    }

    public final List<N> findNames( final ApplicationKey key )
    {
        final List<N> keys = Lists.newArrayList();
        for ( final ResourceKey resource : this.resourceService.findFiles( key, this.path, "xml", true ) )
        {
            final Matcher matcher = this.pattern.matcher( resource.getPath() );
            if ( matcher.matches() )
            {
                final String name = matcher.group( 2 );
                if ( name.equals( matcher.group( 1 ) ) )
                {
                    keys.add( newName( key, name ) );
                }
            }
        }

        return keys;
    }

    protected abstract N newName( final ApplicationKey appKey, final String name );

    protected final Resource getResource( final N name )
    {
        final ResourceKey resourceKey = toResourceKey( name, "xml" );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        return resource;
    }
}
