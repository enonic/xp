package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.data.Value;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Value value;

    public BreaksRequiredContractException( final Value value )
    {
        super( buildMessage( value ) );
        this.value = value;
    }

    public Value getValue()
    {
        return value;
    }

    private static String buildMessage( final Value value )
    {
        return "Required contract for field [" + value.getField() + "] is broken, value was: " + value.getValue();
    }
}
