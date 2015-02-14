package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.Occurrences;

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