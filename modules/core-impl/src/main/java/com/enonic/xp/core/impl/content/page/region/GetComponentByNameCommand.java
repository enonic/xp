package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.page.DescriptorKey;
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

class GetComponentByNameCommand
{
    private ModuleKey module;

    private ComponentName name;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    public Component execute()
    {
        final String componentDescriptorName = name.toString();

        final PartDescriptor partDescriptor = getPartDescriptor( componentDescriptorName );
        if ( partDescriptor != null )
        {
            return PartComponent.newPartComponent().
                name( this.name ).
                descriptor( partDescriptor.getKey() ).
                build();
        }
        final LayoutDescriptor layoutDescriptor = getLayoutDescriptor( componentDescriptorName );
        if ( layoutDescriptor != null )
        {
            return LayoutComponent.newLayoutComponent().
                name( this.name ).
                descriptor( layoutDescriptor.getKey() ).
                build();
        }
        return null;
    }

    private PartDescriptor getPartDescriptor( final String descriptorName )
    {
        try
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( this.module, descriptorName );
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
            final DescriptorKey descriptorKey = DescriptorKey.from( this.module, descriptorName );
            return layoutDescriptorService.getByKey( descriptorKey );
        }
        catch ( LayoutDescriptorNotFoundException e )
        {
            return null;
        }
    }

    public GetComponentByNameCommand module( final ModuleKey module )
    {
        this.module = module;
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
}
