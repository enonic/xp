package com.enonic.wem.api.schema.content.form;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.schema.content.form.inputtype.InputType;

public class BreaksRequiredContractException
    extends RuntimeException
{
    private Property property;

    public BreaksRequiredContractException( final Property property, final InputType inputType )
    {
        super( buildMessage( property, inputType ) );
        this.property = property;
    }

    public BreaksRequiredContractException( final Input missingInput )
    {
        super( buildMessage( missingInput ) );
    }

    public BreaksRequiredContractException( final FormItemSet missingFormItemSet )
    {
        super( buildMessage( missingFormItemSet ) );
    }

    public Property getProperty()
    {
        return property;
    }

    private static String buildMessage( final Property property, final InputType inputType )
    {
        return "Required contract for Data [" + property.getPath() + "] is broken of type " + inputType + " , value was: " +
            property.getObject();
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
