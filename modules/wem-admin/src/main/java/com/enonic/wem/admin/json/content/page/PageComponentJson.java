package com.enonic.wem.admin.json.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.admin.json.content.page.image.ImageComponentJson;
import com.enonic.wem.admin.json.content.page.layout.LayoutComponentJson;
import com.enonic.wem.admin.json.content.page.part.PartComponentJson;
import com.enonic.wem.admin.json.content.page.text.TextComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.text.TextComponent;

@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = ImageComponentJson.class, name = "ImageComponent"),
                  @JsonSubTypes.Type(value = PartComponentJson.class, name = "PartComponent"),
                  @JsonSubTypes.Type(value = LayoutComponentJson.class, name = "LayoutComponent")})
public abstract class PageComponentJson<COMPONENT extends PageComponent>
{
    private final COMPONENT component;

    private final List<DataJson> config;

    protected PageComponentJson( final COMPONENT component )
    {
        this.component = component;
        this.config = component.getConfig() != null ? new RootDataSetJson( component.getConfig() ).getSet() : null;
    }

    public String getName()
    {
        return component.getName().toString();
    }

    public String getDescriptor()
    {
        return component.getDescriptor() != null ? component.getDescriptor().toString() : null;
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
        else if ( component instanceof TextComponent )
        {
            return new TextComponentJson( (TextComponent) component );
        }
        else
        {
            throw new IllegalArgumentException( "PageComponent not supported: " + component.getClass().getSimpleName() );
        }
    }

    @JsonIgnore
    public COMPONENT getComponent()
    {
        return this.component;
    }
}
