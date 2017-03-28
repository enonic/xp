package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorNotFoundException;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorNotFoundException;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

class GetComponentByNameCommand
{
    private ApplicationKey applicationKey;

    private ComponentName name;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ResourceService resourceService;

    public Component execute()
    {
        final String componentDescriptorName = name.toString();

        final PartDescriptor partDescriptor = getPartDescriptor( componentDescriptorName );
        if ( partDescriptor != null && componentExists( partDescriptor.getComponentPath() ) )
        {
            return PartComponent.create().
                name( this.name ).
                descriptor( partDescriptor.getKey() ).
                build();
        }
        final LayoutDescriptor layoutDescriptor = getLayoutDescriptor( componentDescriptorName );
        if ( layoutDescriptor != null && componentExists( layoutDescriptor.getComponentPath() ) )
        {
            return LayoutComponent.create().
                name( this.name ).
                descriptor( layoutDescriptor.getKey() ).
                build();
        }
        return null;
    }

    private boolean componentExists( final ResourceKey componentPath )
    {
        return resourceService.getResource( componentPath ).exists();
    }

    private PartDescriptor getPartDescriptor( final String descriptorName )
    {
        try
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( this.applicationKey, descriptorName );
            return partDescriptorService.getByKey( descriptorKey );
        }
        catch ( PartDescriptorNotFoundException e )
        {
            return null;
        }
    }

    private LayoutDescriptor getLayoutDescriptor( final String descriptorName )
    {
        try
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( this.applicationKey, descriptorName );
            return layoutDescriptorService.getByKey( descriptorKey );
        }
        catch ( LayoutDescriptorNotFoundException e )
        {
            return null;
        }
    }

    public GetComponentByNameCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }

    public GetComponentByNameCommand name( final ComponentName name )
    {
        this.name = name;
        return this;
    }

    public GetComponentByNameCommand partDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
        return this;
    }

    public GetComponentByNameCommand layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
        return this;
    }

    public GetComponentByNameCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }
}
