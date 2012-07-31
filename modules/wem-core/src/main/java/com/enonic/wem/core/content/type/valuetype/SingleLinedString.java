package com.enonic.wem.core.content.type.valuetype;


import com.enonic.wem.core.content.data.Value;

public class SingleLinedString
    extends AbstractValueType
    implements ValueType
{
    public SingleLinedString()
    {
        super( BasalValueType.STRING );
    }

    public boolean validValue( final Value fieldValue )
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
