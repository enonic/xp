package com.enonic.wem.api.content.type.component;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.component.inputtype.InputType;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Data data;

    public BreaksRequiredContractException( final Data data, final InputType inputType )
    {
        super( buildMessage( data, inputType ) );
        this.data = data;
    }

    public BreaksRequiredContractException( final Input missingInput )
    {
        super( buildMessage( missingInput ) );
    }

    public BreaksRequiredContractException( final ComponentSet missingComponentSet )
    {
        super( buildMessage( missingComponentSet ) );
    }

    public Data getValue()
    {
        return data;
    }

    private static String buildMessage( final Data data, final InputType inputType )
    {
        return "Required contract for Data [" + data.getPath() + "] is broken of type " + inputType + " , value was: " +
            data.getValue();
    }

    private static String buildMessage( final Input input )
    {
        return "Required contract is broken, data missing for Input: " + input.getPath().toString();
    }

    private static String buildMessage( final ComponentSet componentSet )
    {
        return "Required contract is broken, data missing for ComponentSet: " + componentSet.getPath().toString();
    }
}
