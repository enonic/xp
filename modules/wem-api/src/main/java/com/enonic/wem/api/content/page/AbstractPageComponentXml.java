package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlAttribute;


public abstract class AbstractPageComponentXml
{
    @XmlAttribute(name = "name", required = true)
    String name;

    public void from( final PageComponent component )
    {
        this.name = component.getName().toString();
    }

    public void to( final AbstractPageComponent.Builder builder )
    {
        builder.name( new ComponentName( this.name ) );
    }

    public abstract PageComponent toPageComponent();
}
