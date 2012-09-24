package com.enonic.wem.core.content.type.formitem.comptype;


import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.comptype.BaseComponentType;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentType;

public class ComponentTypeSerializerXml
{
    public Element generate( final ComponentType componentType )
    {
        final BaseComponentType baseComponentType = (BaseComponentType) componentType;

        final Element componentTypeEl = new Element( "component-type" );
        componentTypeEl.setAttribute( "class-name", baseComponentType.getClassName() );
        return componentTypeEl;
    }

    public BaseComponentType parse( final Element formItemEl )
    {
        Element componentTypeEl = formItemEl.getChild( "component-type" );
        return instantiate( componentTypeEl.getAttributeValue( "class-name" ) );
    }

    private static BaseComponentType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (BaseComponentType) clazz.newInstance();
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
}
