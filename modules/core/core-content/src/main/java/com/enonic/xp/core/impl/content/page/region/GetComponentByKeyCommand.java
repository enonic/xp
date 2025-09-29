package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
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
                descriptor( layoutDescriptor.getKey() ).regions( buildRegions( layoutDescriptor ) ).
                build();
        }
        return null;
    }

    private Regions buildRegions( final LayoutDescriptor layoutDescriptor )
    {
        final Regions.Builder regionsBuilder = Regions.create();
        layoutDescriptor.getRegions()
            .forEach( regionDescriptor -> regionsBuilder.add( Region.create().name( regionDescriptor.getName() ).build() ) );

        return regionsBuilder.build();
    }

    private boolean componentExists( final ResourceKey componentPath )
    {
        return resourceService.getResource( componentPath.resolve( componentPath.getName() + ".xml" ) ).exists();
    }

    private PartDescriptor getPartDescriptor()
    {
        return partDescriptorService.getByKey( descriptorKey );
    }

    private LayoutDescriptor getLayoutDescriptor()
    {
        return layoutDescriptorService.getByKey( descriptorKey );
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
