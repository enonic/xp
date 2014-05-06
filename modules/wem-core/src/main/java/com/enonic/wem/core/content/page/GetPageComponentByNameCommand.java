package com.enonic.wem.core.content.page;


import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorNotFoundException;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorNotFoundException;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorNotFoundException;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.module.ModuleKey;

class GetPageComponentByNameCommand
{
    private ModuleKey module;

    private ComponentName name;

    private PartDescriptorService partDescriptorService;

    private ImageDescriptorService imageDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    public DescriptorBasedPageComponent execute()
    {
        final ComponentDescriptorName componentDescriptorName = new ComponentDescriptorName( name.toString() );

        final PartDescriptor partDescriptor = getPartDescriptor( componentDescriptorName );
        if ( partDescriptor != null )
        {
            return PartComponent.newPartComponent().
                name( this.name ).
                descriptor( partDescriptor.getKey() ).
                build();
        }
        final ImageDescriptor imageDescriptor = getImageDescriptor( componentDescriptorName );
        if ( imageDescriptor != null )
        {
            return ImageComponent.newImageComponent().
                name( this.name ).
                descriptor( imageDescriptor.getKey() ).
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

    private PartDescriptor getPartDescriptor( final ComponentDescriptorName descriptorName )
    {
        try
        {
            final PartDescriptorKey partDescriptorKey = PartDescriptorKey.from( this.module, descriptorName );
            return partDescriptorService.getByKey( partDescriptorKey );
        }
        catch ( PartDescriptorNotFoundException e )
        {
            return null;
        }
    }

    private ImageDescriptor getImageDescriptor( final ComponentDescriptorName descriptorName )
    {
        try
        {
            final ImageDescriptorKey partDescriptorKey = ImageDescriptorKey.from( this.module, descriptorName );
            return imageDescriptorService.getImageDescriptor( partDescriptorKey );
        }
        catch ( ImageDescriptorNotFoundException e )
        {
            return null;
        }
    }

    private LayoutDescriptor getLayoutDescriptor( final ComponentDescriptorName descriptorName )
    {
        try
        {
            final LayoutDescriptorKey partDescriptorKey = LayoutDescriptorKey.from( this.module, descriptorName );
            return layoutDescriptorService.getByKey( partDescriptorKey );
        }
        catch ( LayoutDescriptorNotFoundException e )
        {
            return null;
        }
    }

    public GetPageComponentByNameCommand module( final ModuleKey module )
    {
        this.module = module;
        return this;
    }

    public GetPageComponentByNameCommand name( final ComponentName name )
    {
        this.name = name;
        return this;
    }

    public GetPageComponentByNameCommand partDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
        return this;
    }

    public GetPageComponentByNameCommand imageDescriptorService( final ImageDescriptorService imageDescriptorService )
    {
        this.imageDescriptorService = imageDescriptorService;
        return this;
    }

    public GetPageComponentByNameCommand layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
        return this;
    }
}
