package com.enonic.wem.core.content.type.component.inputtype;


import org.jdom.Element;

import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.api.content.type.component.inputtype.InputType;

public class InputTypeSerializerXml
{
    public Element serialize( final InputType inputType )
    {
        final BaseInputType baseInputType = (BaseInputType) inputType;

        final Element inputTypeEl = new Element( "input-type" );
        inputTypeEl.setAttribute( "name", baseInputType.getName() );
        inputTypeEl.setAttribute( "built-in", String.valueOf( baseInputType.isBuiltIn() ) );
        return inputTypeEl;
    }

    public BaseInputType parse( final Element componentEl )
    {
        final Element inputTypeEl = componentEl.getChild( "input-type" );
        final String name = inputTypeEl.getAttributeValue( "name" );
        final boolean builtIn = Boolean.valueOf( inputTypeEl.getAttributeValue( "built-in" ) );
        return InputTypeFactory.instantiate( name, builtIn );
    }
}
