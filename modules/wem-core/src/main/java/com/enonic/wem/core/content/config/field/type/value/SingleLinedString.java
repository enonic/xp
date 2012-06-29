package com.enonic.wem.core.content.config.field.type.value;


import com.enonic.wem.core.content.FieldValue;

public class SingleLinedString
    implements ValueType
{
    public boolean validValue( final FieldValue fieldValue )
    {
        if ( !( fieldValue.getValue() instanceof String ) )
        {
            return false;
        }

        String stringValue = (String) fieldValue.getValue();
        if ( stringValue != null )
        {
            if ( stringValue.contains( "\r" ) || stringValue.contains( "\n" ) )
            {
                return false;
            }
        }

        return true;
    }
}
