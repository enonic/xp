package com.enonic.xp.core.impl.schema;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;

public abstract class SchemaLoader<N extends BaseSchemaName, V extends BaseSchema>
{
    private final String path;

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    public SchemaLoader( final ResourceService resourceService, final String path )
    {
        this.resourceService = resourceService;
        this.path = path;
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, path, false );
    }

    public final V get( final N name )
    {
        final ResourceProcessor<N, V> processor = newProcessor( name );
        return this.resourceService.processResource( processor );
    }

    protected abstract V load( N name, Resource resource );

    private ResourceProcessor<N, V> newProcessor( final N key )
    {
        return new ResourceProcessor.Builder<N, V>().key( key )
            .segment( key.getClass().getSimpleName() )
            .keyTranslator( this::toYmlResourceKey )
            .processor( resource -> load( key, resource ) )
            .build();
    }

    protected final ResourceKey toYmlResourceKey( final N name )
    {
        final ApplicationKey appKey = name.getApplicationKey();
        final String localName = name.getLocalName();
        return ResourceKey.from( appKey, this.path + "/" + localName + "/" + localName + ".yml" );
    }

    protected final ResourceKey toResourceKey( final N name, final String ext )
    {
        final ApplicationKey appKey = name.getApplicationKey();
        final String localName = name.getLocalName();
        return ResourceKey.from( appKey, this.path + "/" + localName + "/" + localName + "." + ext );
    }

    protected final Icon loadIcon( final N name )
    {
        final Icon svgIcon = loadIcon( name, "image/svg+xml", "svg" );

        if ( svgIcon != null )
        {
            return svgIcon;
        }
        else
        {
            return loadIcon( name, "image/png", "png" );
        }
    }

    private Icon loadIcon( final N name, final String mimeType, final String ext )
    {
        final ResourceKey resourceKey = toResourceKey( name, ext );
        final Resource resource = this.resourceService.getResource( resourceKey );
        return SchemaHelper.loadIcon( resource, mimeType );
    }

    public final Set<N> findNames( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key ).stream().map( this::newName ).collect( Collectors.toCollection( LinkedHashSet::new ) );
    }

    protected abstract N newName( DescriptorKey descriptorKey );
}
