package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageComponentJson;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponentJson;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartComponentJson;
import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.content.page.text.TextComponentJson;

public final class PageComponentJsonSerializer
{
    public static AbstractPageComponentJson toJson( final PageComponent component )
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
