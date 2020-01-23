package com.enonic.xp.admin.impl.json.content.page.region;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.xp.admin.impl.rest.resource.content.ComponentNameResolver;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;

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

    protected final ComponentNameResolver componentNameResolver;

    protected ComponentJson( final COMPONENT component, final ComponentNameResolver componentNameResolver )
    {
        this.component = component;
        this.componentNameResolver = componentNameResolver;
    }

    public String getName()
    {
        final ComponentName name = componentNameResolver != null ? componentNameResolver.resolve( component ) : null;
        return name != null ? name.toString() : null;
    }

    @JsonIgnore
    public COMPONENT getComponent()
    {
        return this.component;
    }
}
