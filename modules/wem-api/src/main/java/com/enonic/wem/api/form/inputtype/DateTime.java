package com.enonic.wem.api.form.inputtype;


import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.Value;
import com.enonic.wem.api.data2.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

public class DateTime
    extends InputType
{
    DateTime()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}