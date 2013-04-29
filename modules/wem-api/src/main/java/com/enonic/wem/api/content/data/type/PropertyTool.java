package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;

public class PropertyTool
{
    public static void checkValueType( final Property property, final ValueType valueType )
        throws InvalidDataException
    {
        if ( !property.getValueType().equals( valueType ) )
        {
            throw new InvalidPropertyTypeException( property, valueType );
        }
    }
}
