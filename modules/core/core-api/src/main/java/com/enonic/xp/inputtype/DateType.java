package com.enonic.xp.inputtype;

import java.time.LocalDate;
import java.time.Period;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;

final class DateType
    extends InputTypeBase
{
    public final static DateType INSTANCE = new DateType();

    private DateType()
    {
        super( InputTypeName.DATE );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newLocalDate( value.asLocalDate() );
    }

    @Override
    public Value createDefaultValue( final InputTypeDefault defaultConfig )
    {
        final String defaultValue = defaultConfig.getRootValue();
        if ( defaultValue != null )
        {
            try
            {
                return ValueFactory.newLocalDate( ValueTypes.LOCAL_DATE.convert( defaultValue ) );
            }
            catch ( ValueTypeException e )
            {
                final RelativeTime result = RelativeTimeParser.parse( defaultValue );

                if ( result != null )
                {
                    final Period period = result.getDate();
                    final LocalDate localDate =
                        LocalDate.now().plusYears( period.getYears() ).plusMonths( period.getMonths() ).plusDays( period.getDays() );
                    return ValueFactory.newLocalDate( localDate );
                }
                else
                {
                    throw new IllegalArgumentException( "Invalid Date format: " + defaultValue );
                }

            }
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LOCAL_DATE );
    }
}
