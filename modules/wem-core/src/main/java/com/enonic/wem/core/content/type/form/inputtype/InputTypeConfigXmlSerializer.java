package com.enonic.wem.core.content.type.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.content.type.form.inputtype.AbstractInputTypeConfigXmlSerializer;
import com.enonic.wem.api.content.type.form.inputtype.InputTypeConfig;

public class InputTypeConfigXmlSerializer
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

        final String serializerClassName = className + "XmlSerializer";

        AbstractInputTypeConfigXmlSerializer parser = instantiateInputTypeConfigXmlParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigEl );
    }

    private AbstractInputTypeConfigXmlSerializer instantiateInputTypeConfigXmlParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractInputTypeConfigXmlSerializer) cls.newInstance();
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
