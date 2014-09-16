package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageComponentXml;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponentXml;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartComponentXml;
import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.content.page.text.TextComponentXml;

public final class PageComponentXmlSerializer
{
    public static AbstractPageComponentXml toXml( final PageComponent component )
    {
        if ( component instanceof LayoutComponent )
        {
            return toXml( (LayoutComponent) component );
        }

        if ( component instanceof TextComponent )
        {
            return toXml( (TextComponent) component );
        }

        if ( component instanceof PartComponent )
        {
            return toXml( (PartComponent) component );
        }

        if ( component instanceof ImageComponent )
        {
            return toXml( (ImageComponent) component );
        }

        throw new IllegalArgumentException( "Xml for component " + component.getType().toString() + " not supported" );
    }

    private static LayoutComponentXml toXml( final LayoutComponent component )
    {
        final LayoutComponentXml componentXml = new LayoutComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    private static TextComponentXml toXml( final TextComponent component )
    {
        final TextComponentXml componentXml = new TextComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    private static PartComponentXml toXml( final PartComponent component )
    {
        final PartComponentXml componentXml = new PartComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    private static ImageComponentXml toXml( final ImageComponent component )
    {
        ImageComponentXml componentXml = new ImageComponentXml();
        componentXml.from( component );
        return componentXml;
    }
}
