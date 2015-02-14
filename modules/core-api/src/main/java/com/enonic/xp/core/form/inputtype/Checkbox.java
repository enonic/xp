package com.enonic.xp.core.form.inputtype;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.data.ValueTypes;
import com.enonic.xp.core.form.BreaksRequiredContractException;
import com.enonic.xp.core.form.Occurrences;

public class Checkbox
    extends InputType
{
    public Checkbox()
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
        return Value.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

    @Override
    public void validateOccurrences( final Occurrences occurrences )
    {
        if ( occurrences.getMinimum() != 0 )
        {
            throw new InvalidOccurrencesConfigurationException(
                "An Input of type " + this.getClass().getSimpleName() + " can only have 0 as minimum occurrences: " +
                    occurrences.getMinimum() );
        }
    }
}