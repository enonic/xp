package com.enonic.xp.admin.impl.json.content.page.region;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.TextComponent;

public final class ComponentJsonSerializer
{
    private ComponentNameResolver componentNameResolver;

    public ComponentJsonSerializer( final ComponentNameResolver componentNameResolver )
    {
        this.componentNameResolver = componentNameResolver;
    }

    public ComponentJson toJson( final Component component )
    {
        if ( component instanceof LayoutComponent )
        {
            return toJson( (LayoutComponent) component );
        }

        if ( component instanceof TextComponent )
        {
            return toJson( (TextComponent) component );
        }

        if ( component instanceof PartComponent )
        {
            return toJson( (PartComponent) component );
        }

        if ( component instanceof ImageComponent )
        {
            return toJson( (ImageComponent) component );
        }

        if ( component instanceof FragmentComponent )
        {
            return toJson( (FragmentComponent) component );
        }

        throw new IllegalArgumentException( "Json for component " + component.getType().toString() + " not supported" );
    }

    private LayoutComponentJson toJson( final LayoutComponent component )
    {
        return new LayoutComponentJson( component, componentNameResolver );
    }

    private TextComponentJson toJson( final TextComponent component )
    {
        return new TextComponentJson( component );
    }

    private PartComponentJson toJson( final PartComponent component )
    {
        return new PartComponentJson( component, componentNameResolver );
    }

    private ImageComponentJson toJson( final ImageComponent component )
    {
        return new ImageComponentJson( component, componentNameResolver );
    }

    private FragmentComponentJson toJson( final FragmentComponent component )
    {
        return new FragmentComponentJson( component, componentNameResolver );
    }
}
