package com.enonic.xp.admin.impl.rest.resource.content;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.TextComponent;

@org.osgi.service.component.annotations.Component
public final class ComponentNameResolverImpl
    implements ComponentNameResolver
{
    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ContentService contentService;

    public ComponentName resolve( final Component component )
    {
        if ( component instanceof PartComponent )
        {
            return resolve( (PartComponent) component );
        }
        else if ( component instanceof LayoutComponent )
        {
            return resolve( (LayoutComponent) component );
        }
        else if ( component instanceof FragmentComponent )
        {
            return resolve( (FragmentComponent) component );
        }
        else if ( component instanceof ImageComponent )
        {
            return resolve( (ImageComponent) component );
        }
        else if ( component instanceof TextComponent )
        {
            return resolve( (TextComponent) component );
        }
        return null;
    }


    public ComponentName resolve( final PartComponent component )
    {
        final PartDescriptor partDescriptor = partDescriptorService.getByKey( component.getDescriptor() );
        return partDescriptor != null ? ComponentName.from( partDescriptor.getDisplayName() ) : null;
    }

    public ComponentName resolve( final LayoutComponent component )
    {
        final LayoutDescriptor layoutDescriptor = layoutDescriptorService.getByKey( component.getDescriptor() );
        return layoutDescriptor != null ? ComponentName.from( layoutDescriptor.getDisplayName() ) : null;
    }

    public ComponentName resolve( final ImageComponent component )
    {
        if ( component.hasImage() )
        {
            final Content content = contentService.getById( component.getImage() );
            return ComponentName.from( content.getDisplayName() );
        }
        return component.getName();
    }

    public ComponentName resolve( final FragmentComponent component )
    {
        if ( component.getFragment() != null )
        {
            final Content content = contentService.getById( component.getFragment() );
            return ComponentName.from( content.getDisplayName() );
        }
        return component.getName();
    }

    public ComponentName resolve( final TextComponent component )
    {
        return component.getName();
    }


    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
