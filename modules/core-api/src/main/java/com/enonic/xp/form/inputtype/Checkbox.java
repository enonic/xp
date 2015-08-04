package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.form.Occurrences;

@Beta
final class Checkbox
    extends InputType
{
    public Checkbox()
    {
        super( "Checkbox", null, false );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.BOOLEAN.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.BOOLEAN );
        }
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

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }
}
