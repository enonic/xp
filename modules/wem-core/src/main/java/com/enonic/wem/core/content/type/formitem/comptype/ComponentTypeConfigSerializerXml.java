package com.enonic.wem.core.content.type.formitem.comptype;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.content.type.formitem.comptype.AbstractComponentTypeConfigSerializerXml;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypeConfig;

public class ComponentTypeConfigSerializerXml
{
    public ComponentTypeConfig parse( final Element componentEl )
    {
        final Element componentTypeConfigEl = componentEl.getChild( "component-type-config" );
        if ( componentTypeConfigEl == null )
        {
            return null;
        }
        final String className = componentTypeConfigEl.getAttributeValue( "name" );
        if ( StringUtils.isBlank( className ) )
        {
            return null;
        }

        final String serializerClassName = className + "SerializerXml";

        AbstractComponentTypeConfigSerializerXml parser = instantiateComponentTypeConfigXmlParser( serializerClassName );
        return parser.parseConfig( componentTypeConfigEl );
    }

    private AbstractComponentTypeConfigSerializerXml instantiateComponentTypeConfigXmlParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractComponentTypeConfigSerializerXml) cls.newInstance();
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
