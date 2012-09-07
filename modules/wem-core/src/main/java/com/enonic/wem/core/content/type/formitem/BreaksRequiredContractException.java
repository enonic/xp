package com.enonic.wem.core.content.type.formitem;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentType;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Data data;

    public BreaksRequiredContractException( final Data data, final ComponentType componentType )
    {
        super( buildMessage( data, componentType ) );
        this.data = data;
    }

    public BreaksRequiredContractException( final Component missingComponent )
    {
        super( buildMessage( missingComponent ) );
    }

    public BreaksRequiredContractException( final FormItemSet missingFormItemSet )
    {
        super( buildMessage( missingFormItemSet ) );
    }

    public Data getValue()
    {
        return data;
    }

    private static String buildMessage( final Data data, final ComponentType componentType )
    {
        return "Required contract for Data [" + data.getPath() + "] is broken of type " + componentType + " , value was: " +
            data.getValue();
    }

    private static String buildMessage( final Component component )
    {
        return "Required contract is broken, data missing for Field: " + component.getPath().toString();
    }

    private static String buildMessage( final FormItemSet formItemSet )
    {
        return "Required contract is broken, data missing for FormItemSet: " + formItemSet.getPath().toString();
    }
}
