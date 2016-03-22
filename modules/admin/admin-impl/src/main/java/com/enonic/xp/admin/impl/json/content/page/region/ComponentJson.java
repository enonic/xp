package com.enonic.xp.admin.impl.json.content.page.region;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.xp.region.Component;

@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = ImageComponentJson.class, name = "ImageComponent"),
    @JsonSubTypes.Type(value = PartComponentJson.class, name = "PartComponent"),
    @JsonSubTypes.Type(value = LayoutComponentJson.class, name = "LayoutComponent"),
    @JsonSubTypes.Type(value = TextComponentJson.class, name = "TextComponent"),
    @JsonSubTypes.Type(value = FragmentComponentJson.class, name = "FragmentComponent")})
public abstract class ComponentJson<COMPONENT extends Component>
{
    private final COMPONENT component;

    protected ComponentJson( final COMPONENT component )
    {
        this.component = component;
    }

    public String getName()
    {
        return component.getName() != null ? component.getName().toString() : null;
    }

    @JsonIgnore
    public COMPONENT getComponent()
    {
        return this.component;
    }
}
