package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.inputtype.InputType;

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

    public BreaksRequiredContractException( final FormItemSet missingFormItemSet )
    {
        super( buildMessage( missingFormItemSet ) );
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

    private static String buildMessage( final FormItemSet formItemSet )
    {
        return "Required contract is broken, data missing for FormItemSet: " + formItemSet.getPath().toString();
    }
}
