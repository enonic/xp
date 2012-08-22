package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.content.data.Data;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Data data;

    public BreaksRequiredContractException( final Data data )
    {
        super( buildMessage( data ) );
        this.data = data;
    }

    public Data getValue()
    {
        return data;
    }

    private static String buildMessage( final Data data )
    {
        return "Required contract for field [" + data.getField() + "] is broken, value was: " + data.getValue();
    }
}
