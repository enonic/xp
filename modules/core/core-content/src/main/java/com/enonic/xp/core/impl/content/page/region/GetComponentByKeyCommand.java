package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.Component;
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

class GetComponentByKeyCommand
{
    private DescriptorKey descriptorKey;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ResourceService resourceService;

    public Component execute()
    {
        final PartDescriptor partDescriptor = getPartDescriptor();
        if ( partDescriptor != null && componentExists( partDescriptor.getComponentPath() ) )
        {
            return PartComponent.create().
                descriptor( partDescriptor.getKey() ).
                build();
        }
        final LayoutDescriptor layoutDescriptor = getLayoutDescriptor();
        if ( layoutDescriptor != null && componentExists( layoutDescriptor.getComponentPath() ) )
        {
            return LayoutComponent.create().
                descriptor( layoutDescriptor.getKey() ).
                build();
        }
        return null;
    }

    private boolean componentExists( final ResourceKey componentPath )
    {
        return resourceService.getResource( componentPath ).exists();
    }

    private PartDescriptor getPartDescriptor()
    {
        try
        {
            return partDescriptorService.getByKey( descriptorKey );
        }
        catch ( PartDescriptorNotFoundException e )
        {
            return null;
        }
    }

    private LayoutDescriptor getLayoutDescriptor()
    {
        try
        {
            return layoutDescriptorService.getByKey( descriptorKey );
        }
        catch ( LayoutDescriptorNotFoundException e )
        {
            return null;
        }
    }

    public GetComponentByKeyCommand descriptorKey( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
        return this;
    }

    public GetComponentByKeyCommand partDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
        return this;
    }

    public GetComponentByKeyCommand layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
        return this;
    }

    public GetComponentByKeyCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }
}
