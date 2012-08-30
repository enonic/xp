package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class SingleLinedString
    extends AbstractDataType
    implements DataType
{
    public SingleLinedString()
    {
        super( BasalValueType.STRING, FieldTypes.TEXT_LINE );
    }

    public boolean validData( final Data data )
    {
        if ( !( data.getValue() instanceof String ) )
        {
            return false;
        }

        String stringValue = (String) data.getValue();
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
