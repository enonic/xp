package com.enonic.wem.core.content.type.formitem.comptype;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.content.type.component.inputtype.AbstractInputTypeConfigSerializerXml;
import com.enonic.wem.api.content.type.component.inputtype.InputTypeConfig;

public class InputTypeConfigSerializerXml
{
    public InputTypeConfig parse( final Element inputEl )
    {
        final Element inputTypeConfigEl = inputEl.getChild( "input-type-config" );
        if ( inputTypeConfigEl == null )
        {
            return null;
        }
        final String className = inputTypeConfigEl.getAttributeValue( "name" );
        if ( StringUtils.isBlank( className ) )
        {
            return null;
        }

        final String serializerClassName = className + "SerializerXml";

        AbstractInputTypeConfigSerializerXml parser = instantiateInputTypeConfigXmlParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigEl );
    }

    private AbstractInputTypeConfigSerializerXml instantiateInputTypeConfigXmlParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractInputTypeConfigSerializerXml) cls.newInstance();
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
