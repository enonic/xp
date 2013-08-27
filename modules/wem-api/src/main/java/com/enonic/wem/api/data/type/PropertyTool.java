package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.form.InvalidDataException;

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
