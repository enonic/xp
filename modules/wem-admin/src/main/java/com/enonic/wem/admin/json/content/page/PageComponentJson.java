package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.enonic.wem.admin.json.content.page.image.ImageComponentJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutComponentJson;
import com.enonic.wem.admin.json.content.page.part.PartComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;

@SuppressWarnings("UnusedDeclaration")
public abstract class PageComponentJson
{
    private final PageComponent component;

    private final List<DataJson> config;

    protected PageComponentJson( final PageComponent component )
    {
        this.component = component;
        this.config = component.getConfig() != null ? new RootDataSetJson( component.getConfig() ).getSet() : null;
    }

    public String getType()
    {
        return this.component.getClass().getSimpleName();
    }

    public String getTemplate()
    {
        return component.getTemplate().toString();
    }

    public List<DataJson> getConfig()
    {
        return config;
    }

    public static PageComponentJson fromPageComponent( final PageComponent component )
    {
        if ( component instanceof ImageComponent )
        {
            return new ImageComponentJson( (ImageComponent) component );
        }
        else if ( component instanceof PartComponent )
        {
            return new PartComponentJson( (PartComponent) component );
        }
        else if ( component instanceof LayoutComponent )
        {
            return new LayoutComponentJson( (LayoutComponent) component );
        }
        else
        {
            throw new IllegalArgumentException( "PageComponent not supported: " + component.getClass().getSimpleName() );
        }
    }
}
