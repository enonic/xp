package com.enonic.wem.core.content.type.formitem.comptype;


import org.jdom.Element;

public abstract class AbstractComponentTypeConfigSerializerXml
{
    public final Element generate( final ComponentTypeConfig config )
    {
        final Element componentTypeConfigEl = new Element( "component-type-config" );
        componentTypeConfigEl.setAttribute( "name", config.getClass().getName() );
        generateConfig( config, componentTypeConfigEl );
        return componentTypeConfigEl;
    }

    public abstract void generateConfig( ComponentTypeConfig config, Element componentTypeConfigEl );


    public abstract ComponentTypeConfig parseConfig( final Element componentTypeConfigEl );
}
