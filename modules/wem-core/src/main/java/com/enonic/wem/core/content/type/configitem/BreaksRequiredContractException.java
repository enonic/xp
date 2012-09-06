package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Data data;

    public BreaksRequiredContractException( final Data data, final FieldType fieldType )
    {
        super( buildMessage( data, fieldType ) );
        this.data = data;
    }

    public BreaksRequiredContractException( final Field missingField )
    {
        super( buildMessage( missingField ) );
    }

    public BreaksRequiredContractException( final FieldSet missingFieldSet )
    {
        super( buildMessage( missingFieldSet ) );
    }

    public Data getValue()
    {
        return data;
    }

    private static String buildMessage( final Data data, final FieldType fieldType )
    {
        return "Required contract for Data [" + data.getPath() + "] is broken of type " + fieldType + " , value was: " + data.getValue();
    }

    private static String buildMessage( final Field field )
    {
        return "Required contract is broken, data missing for Field: " + field.getPath().toString();
    }

    private static String buildMessage( final FieldSet fieldSet )
    {
        return "Required contract is broken, data missing for FieldSet: " + fieldSet.getPath().toString();
    }
}
