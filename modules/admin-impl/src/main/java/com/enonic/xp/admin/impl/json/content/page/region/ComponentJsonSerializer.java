package com.enonic.xp.admin.impl.json.content.page.region;

import com.enonic.xp.page.region.Component;
import com.enonic.xp.page.region.ImageComponent;
import com.enonic.xp.page.region.LayoutComponent;
import com.enonic.xp.page.region.PartComponent;
import com.enonic.xp.page.region.TextComponent;

public final class ComponentJsonSerializer
{
    public static ComponentJson toJson( final Component component )
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

        throw new IllegalArgumentException( "Json for component " + component.getType().toString() + " not supported" );
    }

    private static LayoutComponentJson toJson( final LayoutComponent component )
    {
        return new LayoutComponentJson( component );
    }

    private static TextComponentJson toJson( final TextComponent component )
    {
        return new TextComponentJson( component );
    }

    private static PartComponentJson toJson( final PartComponent component )
    {
        return new PartComponentJson( component );
    }

    private static ImageComponentJson toJson( final ImageComponent component )
    {
        return new ImageComponentJson( component );
    }
}
