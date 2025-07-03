package com.enonic.xp.impl.macro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.form.Form;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlMacroDescriptorParser;

@Component
public final class MacroDescriptorServiceImpl
    implements MacroDescriptorService
{
    private static final String PATH = "/site/macros";

    private final BuiltinMacroDescriptors builtinMacrosDescriptors = new BuiltinMacroDescriptors();

    private final DescriptorKeyLocator descriptorKeyLocator;

    private final ResourceService resourceService;

    private final ApplicationService applicationService;

    @Activate
    public MacroDescriptorServiceImpl( @Reference final ResourceService resourceService,
                                       @Reference final ApplicationService applicationService )
    {
        this.resourceService = resourceService;
        this.applicationService = applicationService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, true );
    }

    @Override
    public MacroDescriptor getByKey( final MacroKey key )
    {
        MacroDescriptor descriptor;
        if ( isSystem( key.getApplicationKey() ) )
        {
            descriptor = builtinMacrosDescriptors.getByKey( key );
        }
        else
        {
            final ResourceProcessor<MacroKey, MacroDescriptor> descriptorProcessor = newDescriptorProcessor( key );
            descriptor = this.resourceService.processResource( descriptorProcessor );
            if ( descriptor == null )
            {
                final ResourceProcessor<MacroKey, MacroDescriptor> controllerProcessor = newControllerProcessor( key );
                descriptor = this.resourceService.processResource( controllerProcessor );
            }
        }

        return descriptor;
    }

    @Override
    public MacroDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        if ( isSystem( applicationKey ) )
        {
            return builtinMacrosDescriptors.getAll();
        }
        else
        {
            return descriptorKeyLocator.findKeys( applicationKey )
                .stream()
                .map( descriptorKey -> getByKey( MacroKey.from( descriptorKey.getApplicationKey(), descriptorKey.getName() ) ) )
                .filter( Objects::nonNull )
                .collect( MacroDescriptors.collecting() );
        }
    }

    @Override
    public MacroDescriptors getByApplications( final ApplicationKeys applicationKeys )
    {
        return applicationKeys.stream().flatMap( ak -> getByApplication( ak ).stream() ).collect( MacroDescriptors.collecting() );
    }

    @Override
    public MacroDescriptors getAll()
    {
        return Stream.concat( builtinMacrosDescriptors.getAll().stream(), this.applicationService.getInstalledApplications()
            .stream()
            .flatMap( a -> getByApplication( a.getKey() ).stream() ) ).collect( MacroDescriptors.collecting() );
    }

    private boolean isSystem( ApplicationKey applicationKey )
    {
        return ApplicationKey.SYSTEM.equals( applicationKey );
    }

    private ResourceProcessor<MacroKey, MacroDescriptor> newDescriptorProcessor( final MacroKey key )
    {
        return new ResourceProcessor.Builder<MacroKey, MacroDescriptor>().key( key )
            .segment( "macroDescriptor" )
            .keyTranslator( MacroDescriptor::toDescriptorResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private ResourceProcessor<MacroKey, MacroDescriptor> newControllerProcessor( final MacroKey key )
    {
        return new ResourceProcessor.Builder<MacroKey, MacroDescriptor>().key( key )
            .segment( "macroDescriptor" )
            .keyTranslator( MacroDescriptor::toControllerResourceKey )
            .processor( resource -> createDefaultDescriptor( key ) )
            .build();
    }

    private void parseXml( final Resource resource, final MacroDescriptor.Builder builder )
    {
        try
        {
            final XmlMacroDescriptorParser parser = new XmlMacroDescriptorParser();
            parser.builder( builder );
            parser.currentApplication( resource.getKey().getApplicationKey() );
            parser.source( resource.readString() );
            parser.parse();
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Could not load macro descriptor [" + resource.getKey() + "]: " + e.getMessage() );
        }
    }

    private MacroDescriptor loadDescriptor( final MacroKey key, final Resource resource )
    {
        final MacroDescriptor.Builder builder = MacroDescriptor.create();

        parseXml( resource, builder );

        builder.key( key ).icon( IconLoader.loadIcon( key, this.resourceService, PATH ) );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );

        return builder.build();
    }

    private MacroDescriptor createDefaultDescriptor( final MacroKey key )
    {
        return MacroDescriptor.create().key( key ).displayName( key.getName() ).form( Form.create().build() ).build();
    }
}
