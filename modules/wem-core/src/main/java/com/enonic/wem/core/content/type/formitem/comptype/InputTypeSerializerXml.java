package com.enonic.wem.core.content.type.formitem.comptype;


import org.jdom.Element;

import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.api.content.type.component.inputtype.InputType;

public class InputTypeSerializerXml
{
    public Element generate( final InputType inputType )
    {
        final BaseInputType baseInputType = (BaseInputType) inputType;

        final Element inputTypeEl = new Element( "input-type" );
        inputTypeEl.setAttribute( "class-name", baseInputType.getClassName() );
        return inputTypeEl;
    }

    public BaseInputType parse( final Element formItemEl )
    {
        Element inputTypeEl = formItemEl.getChild( "input-type" );
        return instantiate( inputTypeEl.getAttributeValue( "class-name" ) );
    }

    private static BaseInputType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (BaseInputType) clazz.newInstance();
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
