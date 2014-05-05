package com.enonic.wem.api.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.content.page.image.ImageComponentJson;
import com.enonic.wem.api.content.page.layout.LayoutComponentJson;
import com.enonic.wem.api.content.page.part.PartComponentJson;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.RootDataSetJson;

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

    public static PageComponentJson fromPageComponent( final PageComponent component )
    {
        return component.getType().toJson( component );
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

    @JsonIgnore
    public COMPONENT getComponent()
    {
        return this.component;
    }
}
