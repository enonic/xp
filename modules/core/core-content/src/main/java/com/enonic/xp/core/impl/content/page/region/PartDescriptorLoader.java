package com.enonic.xp.core.impl.content.page.region;

import java.time.Instant;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlPartDescriptorParser;

@Component(immediate = true)
public class PartDescriptorLoader
    implements DescriptorLoader<PartDescriptor>
{
    private static final String PATH = "/site/parts";

    private final ResourceService resourceService;

    private final MixinService mixinService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public PartDescriptorLoader( @Reference final ResourceService resourceService, @Reference final MixinService mixinService )
    {
        this.resourceService = resourceService;
        this.mixinService = mixinService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( this.resourceService, PATH, true );
    }

    @Override
    public Class<PartDescriptor> getType()
    {
        return PartDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {

        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    public PartDescriptor load( final DescriptorKey key, final Resource resource )
        throws Exception
    {
        final PartDescriptor.Builder builder = PartDescriptor.create();
        parseXml( resource, builder );
        return builder.key( key ).icon( loadIcon( key ) ).build();
    }

    @Override
    public PartDescriptor createDefault( final DescriptorKey key )
    {
        return PartDescriptor.create().key( key ).displayName( key.getName() ).config( Form.empty() ).build();
    }

    @Override
    public PartDescriptor postProcess( final PartDescriptor descriptor )
    {
        return PartDescriptor.copyOf( descriptor ).config( this.mixinService.inlineFormItems( descriptor.getConfig() ) ).build();
    }

    private void parseXml( final Resource resource, final PartDescriptor.Builder builder )
    {
        try
        {
            final XmlPartDescriptorParser parser = new XmlPartDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );

            final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
            builder.modifiedTime( modifiedTime );

            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load part descriptor [" + resource.getKey() + "]: " + e.getMessage() );
        }
    }

    protected final Icon loadIcon( final DescriptorKey name )
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

    private Icon loadIcon( final DescriptorKey key, final String mimeType, final String ext )
    {
        final ResourceKey resourceKey = PartDescriptor.toResourceKey( key, ext );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        return Icon.from( resource.readBytes(), mimeType, modifiedTime );
    }
}
