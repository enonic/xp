package com.enonic.xp.core.impl.style;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.content.parser.YmlStyleDescriptorParser;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleNotFoundException;
import com.enonic.xp.style.ImageStyleSettings;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

@Component(immediate = true)
@NullMarked
public class StyleDescriptorServiceImpl
    implements StyleDescriptorService
{
    private static final String STYLE_DESCRIPTOR_PATH_YAML = "cms/style/style.yaml";

    private static final String STYLE_DESCRIPTOR_PATH_YML = "cms/style/style.yml";

    private ResourceService resourceService;

    private ApplicationService applicationService;

    @Override
    public ImageStyle getImageStyle( final DescriptorKey key )
    {
        final StyleDescriptor descriptor = getByApplication( key.getApplicationKey() );
        final ImageStyle style = descriptor == null ? null : descriptor.getElements().stream()
            .filter( element -> element instanceof ImageStyle && element.getName().equals( key.getName() ) )
            .map( ImageStyle.class::cast ).findFirst().orElse( null );
        if ( style == null )
        {
            throw new ImageStyleNotFoundException( key.toString() );
        }
        ImageStyleSettings.from( style );
        return style;
    }

    @Override
    public @Nullable StyleDescriptor getByApplication( final ApplicationKey applicationKey )
    {
        final ResourceProcessor<ApplicationKey, StyleDescriptor> processor = newProcessor( applicationKey );
        return this.resourceService.processResource( processor );
    }

    @Override
    public StyleDescriptors getByApplications( final ApplicationKeys applicationKeys )
    {
        return applicationKeys.stream().map( this::getByApplication ).filter( Objects::nonNull ).collect( StyleDescriptors.collector() );
    }

    @Override
    public StyleDescriptors getAll()
    {
        return this.applicationService.getInstalledApplications()
            .stream()
            .map( Application::getKey )
            .map( this::getByApplication )
            .filter( Objects::nonNull )
            .collect( StyleDescriptors.collector() );
    }

    private ResourceProcessor<ApplicationKey, StyleDescriptor> newProcessor( final ApplicationKey applicationKey )
    {
        return new ResourceProcessor.Builder<ApplicationKey, StyleDescriptor>().key( applicationKey )
            .segment( "styleDescriptor" )
            .keyTranslator( this::toResourceKey )
            .processor( resource -> loadDescriptor( applicationKey, resource ) )
            .build();
    }

    private StyleDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        return YmlStyleDescriptorParser.parse( resource.readString(), key )
            .modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) )
            .build();
    }

    private ResourceKey toResourceKey( final ApplicationKey key )
    {
        final ResourceKey yamlKey = ResourceKey.from( key, STYLE_DESCRIPTOR_PATH_YAML );
        if ( resourceService.getResource( yamlKey ).exists() )
        {
            return yamlKey;
        }
        return ResourceKey.from( key, STYLE_DESCRIPTOR_PATH_YML );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
