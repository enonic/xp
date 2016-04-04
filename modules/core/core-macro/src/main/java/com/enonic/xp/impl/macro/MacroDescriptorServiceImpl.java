package com.enonic.xp.impl.macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.DescriptorKeyLocator;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

@Component
public final class MacroDescriptorServiceImpl
    implements MacroDescriptorService
{

    private final static String PATH = "/site/macros";

    private ResourceService resourceService;

    @Override
    public MacroDescriptor getByKey( final MacroKey key )
    {
        final ResourceProcessor<MacroKey, MacroDescriptor> processor = newProcessor( key );
        final MacroDescriptor descriptor = this.resourceService.processResource( processor );
        if ( descriptor != null )
        {
            return descriptor;
        }

        return createDefaultDescriptor( key );
    }

    private ResourceProcessor<MacroKey, MacroDescriptor> newProcessor( final MacroKey key )
    {
        return new ResourceProcessor.Builder<MacroKey, MacroDescriptor>().
            key( key ).
            segment( "macroDescriptor" ).
            keyTranslator( MacroDescriptor::toResourceKey ).
            processor( resource -> loadDescriptor( key, resource ) ).
            build();
    }

    private MacroDescriptor createDefaultDescriptor( final MacroKey key )
    {
        return MacroDescriptor.
            create().
            key( key ).
            build();
    }

    private MacroDescriptor loadDescriptor( final MacroKey key, final Resource resource )
    {
        return MacroDescriptor.create().key( key ).build();
    }

    @Override
    public MacroDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        final List<MacroDescriptor> list = Lists.newArrayList();
        for ( final DescriptorKey descriptorKey : findDescriptorKeys( applicationKey ) )
        {
            final MacroKey macroKey = MacroKey.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
            final MacroDescriptor descriptor = getByKey( macroKey );
            if ( descriptor != null )
            {
                list.add( descriptor );
            }

        }

        return MacroDescriptors.from( list );
    }

    private Set<DescriptorKey> findDescriptorKeys( final ApplicationKey key )
    {
        return new DescriptorKeyLocator( this.resourceService, PATH, true ).findKeys( key );
    }

    @Override
    public MacroDescriptors getByApplications( final ApplicationKeys applicationKeys )
    {
        final List<MacroDescriptor> list = new ArrayList<>();
        for ( final ApplicationKey key : applicationKeys )
        {
            list.addAll( getByApplication( key ).getSet() );
        }

        return MacroDescriptors.from( list );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}